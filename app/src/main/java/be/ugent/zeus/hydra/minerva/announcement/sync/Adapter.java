package be.ugent.zeus.hydra.minerva.announcement.sync;

import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.database.SQLException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import be.ugent.zeus.hydra.fragments.preferences.MinervaFragment;
import be.ugent.zeus.hydra.minerva.announcement.AnnouncementDao;
import be.ugent.zeus.hydra.minerva.announcement.AnnouncementNotificationBuilder;
import be.ugent.zeus.hydra.minerva.auth.MinervaConfig;
import be.ugent.zeus.hydra.minerva.course.CourseDao;
import be.ugent.zeus.hydra.minerva.requests.AnnouncementsRequest;
import be.ugent.zeus.hydra.minerva.requests.WhatsNewRequest;
import be.ugent.zeus.hydra.minerva.sync.MinervaAdapter;
import be.ugent.zeus.hydra.minerva.sync.SyncBroadcast;
import be.ugent.zeus.hydra.minerva.sync.SyncUtils;
import be.ugent.zeus.hydra.minerva.sync.Synchronisation;
import be.ugent.zeus.hydra.models.minerva.Announcement;
import be.ugent.zeus.hydra.models.minerva.Announcements;
import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.models.minerva.WhatsNew;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;
import org.threeten.bp.ZonedDateTime;

import java.util.*;

/**
 * The sync adapter for Minerva announcements.
 *
 * The sync adapter will broadcast it's progress, so you can subscribe to be updated. See {@link SyncBroadcast}.
 *
 * @author Niko Strijbol
 */
public class Adapter extends MinervaAdapter {

    private static final String TAG = "AnnouncementSyncAdapter";

    private AnnouncementDao dao;

    Adapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    protected void onPerformCheckedSync(Account account,
                                        Bundle extras,
                                        String authority,
                                        ContentProviderClient provider,
                                        SyncResult results,
                                        boolean isFirstSync) throws RequestFailureException {

        //Get access to the data
        dao = new AnnouncementDao(getContext());
        final CourseDao courseDao = new CourseDao(getContext());

        // If this is the first request, clean everything.
        if (isFirstSync) {
            dao.deleteAll();
        }

        List<Course> courses = courseDao.getAll();

        try {
            // Synchronize announcements for each course. There is no method to do this in one request.
            for (int i = 0; i < courses.size(); i++) {
                Course course = courses.get(i);

                if (isCancelled) {
                    broadcast.publishIntent(SyncBroadcast.SYNC_CANCELLED);
                    return;
                }

                Log.d(TAG, "Syncing course " + course.getId());

                // Sync announcements
                Collection<Announcement> newOnes = synchronizeAnnouncements(account, isFirstSync, course);

                // Publish progress
                broadcast.publishAnnouncementDone(i + 1, courses.size(), course);

                // If not the first time, show notifications
                if (!isFirstSync) {
                    notifyUser(newOnes);
                }
            }
        } catch (SQLException e) {
            // This is thrown when the foreign key fails.
            // This means we have an agenda item for a course that doesn't exist!
            // We abort the synchronisation, and launch the course synchronisation.
            Bundle bundle = new Bundle();
            bundle.putBoolean(be.ugent.zeus.hydra.minerva.course.sync.Adapter.EXTRA_SCHEDULE_ANNOUNCEMENTS, true);
            SyncUtils.requestSync(account, MinervaConfig.COURSE_AUTHORITY, bundle);

            broadcast.publishIntent(SyncBroadcast.SYNC_ERROR);
        }
    }

    /**
     * Show notifications to the user.
     *
     * @param newAnnouncements The announcements to show. Can be empty, but not null.
     */
    private void notifyUser(@NonNull Collection<Announcement> newAnnouncements) {

        if (newAnnouncements.isEmpty()) {
            return;
        }

        // If we may not notify the user, stop here.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (!preferences.getBoolean(MinervaFragment.PREF_ANNOUNCEMENT_NOTIFICATION, true)) {
            return;
        }

        AnnouncementNotificationBuilder builder = new AnnouncementNotificationBuilder(getContext());
        builder.setAnnouncements(newAnnouncements);
        builder.setCourse(newAnnouncements.iterator().next().getCourse());
        builder.publish();
    }

    /**
     * Synchronize the announcements.
     */
    private Collection<Announcement> synchronizeAnnouncements(Account account, boolean first, Course course) throws RequestFailureException {

        // First we get all announcements for the course.
        AnnouncementsRequest announcementsRequest = new AnnouncementsRequest(getContext(), account, course);
        Announcements announcements = announcementsRequest.performRequest();

        // Get all existing announcements
        Map<Integer, ZonedDateTime> existing = dao.getIdsAndReadDateForCourse(course);

        // We calculate the diff
        Synchronisation<Announcement, Integer> synchronisation = new Synchronisation<>(
                existing.keySet(),
                announcements.getAnnouncements(),
                Announcement::getItemId
        );
        Synchronisation.Diff<Announcement, Integer> diff = synchronisation.diff();

        // Then we see what requests are actually new.
        WhatsNewRequest whatsNewRequest = new WhatsNewRequest(course, getContext(), account);
        request = whatsNewRequest;
        WhatsNew whatsNew = whatsNewRequest.performRequest();

        Set<Announcement> unReadOnes = new HashSet<>(whatsNew.getAnnouncements());
        List<Announcement> unread = new ArrayList<>();

        // Check if we need to show emailed or not
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        final boolean showEmail = pref.getBoolean(MinervaFragment.PREF_ANNOUNCEMENT_NOTIFICATION_EMAIL, MinervaFragment.PREF_DEFAULT_ANNOUNCEMENT_NOTIFICATION_EMAIL);

        ZonedDateTime now = ZonedDateTime.now();
        // Modify updated
        for (Announcement announcement: diff.getUpdated()) {
            announcement.setCourse(course);
            // If the announcement is already read, recover the existing read date
            if (!unReadOnes.contains(announcement)) {
                if (existing.containsKey(announcement.getItemId())) {
                    announcement.setRead(existing.get(announcement.getItemId()));
                } else {
                    announcement.setRead(now);
                }
            } else {
                if (showEmail) {
                    unread.add(announcement);
                }
            }
        }

        for (Announcement announcement: diff.getNew()) {
            announcement.setCourse(course);
            if (!unReadOnes.contains(announcement)) {
                announcement.setRead(now);
            } else {
                if (showEmail) {
                    unread.add(announcement);
                }
            }
        }

        // Do the actual synchronisation
        diff.apply(dao);

        return unread;
    }

    @Override
    protected void afterSync(Account account, Bundle extras, boolean isFirstSync) {
        if (isFirstSync) {
            SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(getContext());
            int freq = Integer.parseInt(p.getString(MinervaFragment.PREF_SYNC_FREQUENCY_ANNOUNCEMENT, MinervaFragment.PREF_DEFAULT_SYNC_FREQUENCY));
            SyncUtils.enable(account, MinervaConfig.ANNOUNCEMENT_AUTHORITY, freq);
        }
    }
}