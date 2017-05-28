package be.ugent.zeus.hydra.data.sync.announcement;

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
import be.ugent.zeus.hydra.ui.preferences.MinervaFragment;
import be.ugent.zeus.hydra.data.database.minerva.AnnouncementDao;
import be.ugent.zeus.hydra.data.auth.MinervaConfig;
import be.ugent.zeus.hydra.data.database.minerva.CourseDao;
import be.ugent.zeus.hydra.data.network.requests.minerva.AnnouncementsRequest;
import be.ugent.zeus.hydra.data.network.requests.minerva.WhatsNewRequest;
import be.ugent.zeus.hydra.data.sync.MinervaAdapter;
import be.ugent.zeus.hydra.data.sync.SyncBroadcast;
import be.ugent.zeus.hydra.data.sync.SyncUtils;
import be.ugent.zeus.hydra.data.sync.Synchronisation;
import be.ugent.zeus.hydra.data.models.minerva.Announcement;
import be.ugent.zeus.hydra.data.models.minerva.Announcements;
import be.ugent.zeus.hydra.data.models.minerva.Course;
import be.ugent.zeus.hydra.data.models.minerva.WhatsNew;
import be.ugent.zeus.hydra.data.network.exceptions.RequestFailureException;
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
            bundle.putBoolean(be.ugent.zeus.hydra.data.sync.course.Adapter.EXTRA_SCHEDULE_ANNOUNCEMENTS, true);
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
        Announcements announcements = announcementsRequest.performRequest(null);

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
        WhatsNew whatsNew = whatsNewRequest.performRequest(null);

        Set<Announcement> unReadOnes = new HashSet<>(whatsNew.getAnnouncements());
        List<Announcement> unread = new ArrayList<>();

        // Check if we need to show emailed or not
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        final boolean showEmail = pref.getBoolean(MinervaFragment.PREF_ANNOUNCEMENT_NOTIFICATION_EMAIL, MinervaFragment.PREF_DEFAULT_ANNOUNCEMENT_NOTIFICATION_EMAIL);

        ZonedDateTime now = ZonedDateTime.now();

        // Synchronise the read status for existing announcements.
        for (Announcement announcement: diff.getUpdated()) {
            announcement.setCourse(course);
            if (unReadOnes.contains(announcement)) {
                // Recover the read date from the existing announcement. Otherwise the data will be lost.
                announcement.setRead(existing.get(announcement.getItemId()));
            } else {
                // Apply the read changes from Minerva, as the announcement might still be marked
                // unread in our local database.
                announcement.setRead(now);
            }
        }

        // Synchronise the read statement for new announcements, and filter potential notifications.
        for (Announcement announcement: diff.getNew()) {
            announcement.setCourse(course);
            if (unReadOnes.contains(announcement)) {
                // The announcement is new and not read, so this a potential notification.
                if (showEmail || !announcement.isEmailSent()) {
                    // The announcement did not have an e-mail, or we must notify for e-mails also.
                    unread.add(announcement);
                }
            } else {
                // The announcement was new, but already read.
                announcement.setRead(now);
            }
        }

        // Do the actual synchronisation with our database.
        diff.apply(dao);

        return unread;
    }

    @Override
    protected void afterSync(Account account, Bundle extras, boolean isFirstSync) {
        // If this is the first time we synchronise, we enable automatic synchronisation.
        if (isFirstSync) {
            long frequency = SyncUtils.frequencyFor(getContext(), MinervaConfig.ANNOUNCEMENT_AUTHORITY);
            SyncUtils.enable(account, MinervaConfig.ANNOUNCEMENT_AUTHORITY, frequency);
        }
    }
}