package be.ugent.zeus.hydra.minerva.sync;

import android.accounts.Account;
import android.content.*;
import android.database.SQLException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import be.ugent.zeus.hydra.fragments.preferences.MinervaFragment;
import be.ugent.zeus.hydra.minerva.agenda.AgendaDao;
import be.ugent.zeus.hydra.minerva.announcement.AnnouncementDao;
import be.ugent.zeus.hydra.minerva.announcement.AnnouncementNotificationBuilder;
import be.ugent.zeus.hydra.minerva.course.CourseDao;
import be.ugent.zeus.hydra.models.minerva.Announcement;
import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.models.minerva.Courses;
import be.ugent.zeus.hydra.models.minerva.WhatsNew;
import be.ugent.zeus.hydra.requests.common.RequestFailureException;
import be.ugent.zeus.hydra.requests.minerva.AgendaRequest;
import be.ugent.zeus.hydra.requests.minerva.CoursesMinervaRequest;
import be.ugent.zeus.hydra.requests.minerva.WhatsNewRequest;
import org.joda.time.DateTime;

import java.util.Collection;

/**
 * The sync adapter for minerva stuff.
 *
 * @author Niko Strijbol
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = "SyncAdapter";

    public static final String ARG_FIRST_SYNC = "firstSync";
    public static final String ARG_SEND_BROADCASTS = "sendBroadcasts";

    private boolean cancelled = false;
    private final SyncBroadcast broadcast;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        broadcast = new SyncBroadcast(getContext());
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        broadcast = new SyncBroadcast(getContext());
    }

    //TODO write a good, more formal overview of the sync algorithm.
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        Log.d(TAG, "Starting sync...");
        broadcast.setArgs(extras);
        broadcast.publishIntent(SyncBroadcast.SYNC_START);

        if(cancelled) {
            broadcast.publishIntent(SyncBroadcast.SYNC_CANCELLED);
            return;
        }

        //Get if this is the first sync.
        final boolean first = extras.getBoolean(ARG_FIRST_SYNC, false);

        CoursesMinervaRequest request = new CoursesMinervaRequest(getContext(), account, null);

        final CourseDao courseDao = new CourseDao(getContext());
        final AnnouncementDao announcementDao = new AnnouncementDao(getContext());
        final AgendaDao agendaDao = new AgendaDao(getContext());

        try {
            Courses courses = request.performRequest();
            courseDao.synchronise(courses.getCourses());

            //Synchronise agenda
            AgendaRequest agendaRequest = new AgendaRequest(getContext(), account, null);
            DateTime now = DateTime.now();
            //Start time
            agendaRequest.setStart(now.withTimeAtStartOfDay().toDate());
            //End time. We take 1 month (+1 day for the start time).
            agendaRequest.setEnd(now.plusMonths(1).plusDays(1).withTimeAtStartOfDay().toDate());

            agendaDao.replace(agendaRequest.performRequest().getItems());

            //Publish progress
            broadcast.publishIntent(SyncBroadcast.SYNC_PROGRESS_COURSES);

            //Add info
            for (int i = 0; i < courses.getCourses().size(); i++) {
                Course course = courses.getCourses().get(i);
                if (cancelled) {
                    broadcast.publishIntent(SyncBroadcast.SYNC_CANCELLED);
                    return;
                }
                //We don't add the course to announcements here, since that would be inefficient.
                Log.d(TAG, "Syncing course " + course.getId());
                WhatsNewRequest newRequest = new WhatsNewRequest(course, getContext(), null);
                WhatsNew w = newRequest.performRequest();

                //Sync announcements
                Collection<Announcement> newOnes = announcementDao.synchronisePartial(w.getAnnouncements(), course, first, getContext());

                //Publish progress
                broadcast.publishAnnouncementDone(i + 1, courses.getCourses().size());

                //If not the first time, show notifications
                if (!first) {
                    notifyUser(newOnes);
                }
            }

            broadcast.publishIntent(SyncBroadcast.SYNC_DONE);
        } catch (RequestFailureException e) {
            Log.w(TAG, "Sync error.", e);
            //Adjust stats
            if(e.getCause() != null) {
                if(e.getCause() instanceof RequestFailureException) {
                    syncResult.stats.numIoExceptions++;
                } else {
                    syncResult.stats.numParseExceptions++;
                }
            }
            SyncNotificationBuilder.showError(getContext());
            broadcast.publishIntent(SyncBroadcast.SYNC_ERROR);
        } catch (SQLException e) {
            syncResult.databaseError = true;
            Log.w(TAG, "Sync error.", e);
            SyncNotificationBuilder.showError(getContext());
            broadcast.publishIntent(SyncBroadcast.SYNC_ERROR);
        }
    }

    @Override
    public void onSyncCanceled() {
        super.onSyncCanceled();
        this.cancelled = true;
    }

    /**
     * Show notifications to the user.
     *
     * @param newAnnouncements The announcements to show. Can be empty, but not null.
     */
    private void notifyUser(@NonNull Collection<Announcement> newAnnouncements) {

        if(newAnnouncements.isEmpty()) {
            return;
        }

        //If we may not notify the user, stop here
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if(!preferences.getBoolean(MinervaFragment.PREF_ANNOUNCEMENT_NOTIFICATION, true)) {
            return;
        }

        AnnouncementNotificationBuilder builder = new AnnouncementNotificationBuilder(getContext().getApplicationContext());
        builder.setAnnouncements(newAnnouncements);
        builder.setCourse(newAnnouncements.iterator().next().getCourse());
        //TODO: limit to not every course!
        builder.publish();
    }
}