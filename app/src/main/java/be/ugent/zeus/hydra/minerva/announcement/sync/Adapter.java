package be.ugent.zeus.hydra.minerva.announcement.sync;

import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import be.ugent.zeus.hydra.fragments.preferences.MinervaFragment;
import be.ugent.zeus.hydra.minerva.announcement.AnnouncementDao;
import be.ugent.zeus.hydra.minerva.announcement.AnnouncementNotificationBuilder;
import be.ugent.zeus.hydra.minerva.announcement.SyncObject;
import be.ugent.zeus.hydra.minerva.requests.AnnouncementsRequest;
import be.ugent.zeus.hydra.minerva.requests.WhatsNewRequest;
import be.ugent.zeus.hydra.minerva.sync.MinervaAdapter;
import be.ugent.zeus.hydra.minerva.sync.SyncBroadcast;
import be.ugent.zeus.hydra.models.minerva.*;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;

import java.util.Collection;

/**
 * The sync adapter for Minerva announcements.
 *
 * The sync adapter will broadcast it's progress, so you can subscribe to be updated. See {@link SyncBroadcast}.
 *
 * @author Niko Strijbol
 */
public class Adapter extends MinervaAdapter {

    private static final String TAG = "AnnouncementSyncAdapter";

    Adapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    protected void onPerformCheckedSync(Courses courses,
                                        Account account,
                                        Bundle extras,
                                        String authority,
                                        ContentProviderClient provider,
                                        SyncResult results,
                                        boolean isFirstSync) throws RequestFailureException {

        //Get access to the data
        final AnnouncementDao announcementDao = new AnnouncementDao(getContext());

        // If this is the first request, clean everything.
        if (isFirstSync) {
            announcementDao.deleteAll();
        }

        // Synchronize announcements for each course. There is no method to do this in one request.
        for (int i = 0; i < courses.getCourses().size(); i++) {
            Course course = courses.getCourses().get(i);

            if (isCancelled) {
                broadcast.publishIntent(SyncBroadcast.SYNC_CANCELLED);
                return;
            }

            Log.d(TAG, "Syncing course " + course.getId());

            // Sync announcements
            Collection<Announcement> newOnes = synchronizeAnnouncements(announcementDao, account, isFirstSync, course);

            // Publish progress
            broadcast.publishAnnouncementDone(i + 1, courses.getCourses().size(), course);

            // If not the first time, show notifications
            if (!isFirstSync) {
                notifyUser(newOnes);
            }
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
    private Collection<Announcement> synchronizeAnnouncements(AnnouncementDao dao, Account account, boolean first, Course course) throws RequestFailureException {

        // First we get all courses. The dao marks the new ones as unread.
        AnnouncementsRequest announcementsRequest = new AnnouncementsRequest(getContext(), account, null, course);
        Announcements announcements = announcementsRequest.performRequest();

        WhatsNewRequest whatsNewRequest = new WhatsNewRequest(course, getContext(), account);
        WhatsNew whatsNew = whatsNewRequest.performRequest();

        // Construct object
        SyncObject object = new SyncObject.Builder(course)
                .allObjects(announcements.getAnnouncements())
                .newObjects(whatsNew.getAnnouncements())
                .setFirstSync(first)
                .build();

        //Sync announcements
        return dao.synchronize(object);
    }
}