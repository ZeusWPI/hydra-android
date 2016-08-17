package be.ugent.zeus.hydra.minerva.sync;

import android.accounts.Account;
import android.content.*;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;

import be.ugent.zeus.hydra.minerva.announcement.AnnouncementDao;
import be.ugent.zeus.hydra.minerva.announcement.AnnouncementNotificationBuilder;
import be.ugent.zeus.hydra.minerva.course.CourseDao;
import be.ugent.zeus.hydra.models.minerva.Announcement;
import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.models.minerva.Courses;
import be.ugent.zeus.hydra.models.minerva.WhatsNew;
import be.ugent.zeus.hydra.requests.common.RequestFailureException;
import be.ugent.zeus.hydra.requests.minerva.CoursesMinervaRequest;
import be.ugent.zeus.hydra.requests.minerva.WhatsNewRequest;

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

    public static final String BROADCAST_SYNC_START = "be.ugent.zeus.minerva.broadcast.start";
    public static final String BROADCAST_SYNC_DONE = "be.ugent.zeus.minerva.broadcast.done";
    public static final String BROADCAST_SYNC_ERROR = "be.ugent.zeus.minerva.broadcast.error";
    public static final String BROADCAST_SYNC_CANCELLED = "be.ugent.minerva.broadcast.cancelled";

    public static final String BROADCAST_SYNC_PROGRESS_COURSES = "be.ugent.zeus.minerva.broadcast.progress.courses";
    public static final String BROADCAST_SYNC_PROGRESS_WHATS_NEW = "be.ugent.zeus.minerva.broadcast.progress.data";

    public static final String BROADCAST_ARG_SYNC_PROGRESS_TOTAL = "argTotal";
    public static final String BROADCAST_ARG_SYNC_PROGRESS_CURRENT = "argNow";

    private boolean cancelled = false;



    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        Log.d(TAG, "Starting sync...");
        publishIntent(BROADCAST_SYNC_START, extras);

        if(cancelled) {
            publishIntent(BROADCAST_SYNC_CANCELLED, extras);
            return;
        }

        final boolean first = extras.getBoolean(ARG_FIRST_SYNC, false);

        CoursesMinervaRequest request = new CoursesMinervaRequest(getContext(), account, null);

        try {
            Courses courses = request.performRequest();

            CourseDao dao = new CourseDao(getContext());
            AnnouncementDao announcementDao = new AnnouncementDao(getContext());
            dao.synchronise(courses.getCourses());

            //Publish progress
            publishCoursesDone(extras);

            //Add announcements
            for (int i = 0; i < courses.getCourses().size(); i++) {
                Course course = courses.getCourses().get(i);
                if (cancelled) {
                    publishIntent(BROADCAST_SYNC_CANCELLED, extras);
                    return;
                }
                //We don't add the course to announcements here, since that would be inefficient.
                Log.d(TAG, "Syncing course " + course.getId());
                WhatsNewRequest newRequest = new WhatsNewRequest(course, getContext(), null);
                WhatsNew w = newRequest.performRequest();
                Collection<Announcement> newOnes = announcementDao.synchronisePartial(w.getAnnouncements(), course, first);

                //Publish progress
                publishAnnouncementDone(i + 1, courses.getCourses().size(), extras);

                //If not the first time, show notifications
                if (!first) {
                    notifyUser(newOnes);
                }
            }
            publishIntent(BROADCAST_SYNC_DONE, extras);
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
            publishIntent(BROADCAST_SYNC_ERROR, extras);
        } catch (SQLException e) {
            syncResult.databaseError = true;
            Log.w(TAG, "Sync error.", e);
            publishIntent(BROADCAST_SYNC_ERROR, extras);
        }
    }

    private void notifyUser(Collection<Announcement> newAnnouncements) {

        if(newAnnouncements.isEmpty()) {
            return;
        }

        AnnouncementNotificationBuilder builder = new AnnouncementNotificationBuilder(getContext().getApplicationContext());
        builder.setAnnouncements(newAnnouncements);
        builder.setCourse(newAnnouncements.iterator().next().getCourse());
        //TODO: limit to not every course!
        builder.publish();
    }

    @Override
    public void onSyncCanceled() {
        super.onSyncCanceled();
        this.cancelled = true;
    }

    private void publishIntent(String action, Bundle args) {
        publishIntentWith(action, args, null);
    }

    private void publishIntentWith(String action, Bundle args, Bundle extras) {
        //Don't send if we don't need to.
        if(!args.getBoolean(ARG_SEND_BROADCASTS, false)) {
            return;
        }

        //The intent
        Intent i = new Intent(action);
        if(extras != null) {
            i.putExtras(extras);
        }

        getContext().sendBroadcast(i);
    }

    private void publishCoursesDone(Bundle args) {
        publishIntent(BROADCAST_SYNC_PROGRESS_COURSES, args);
    }

    private void publishAnnouncementDone(int now, int total, Bundle args) {
        Bundle bundle = new Bundle();
        bundle.putInt(BROADCAST_ARG_SYNC_PROGRESS_TOTAL, total);
        bundle.putInt(BROADCAST_ARG_SYNC_PROGRESS_CURRENT, now);
        publishIntentWith(BROADCAST_SYNC_PROGRESS_WHATS_NEW, args, bundle);
    }

    public static IntentFilter getBroadcastFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_SYNC_START);
        filter.addAction(BROADCAST_SYNC_PROGRESS_COURSES);
        filter.addAction(BROADCAST_SYNC_PROGRESS_WHATS_NEW);
        filter.addAction(BROADCAST_SYNC_DONE);
        filter.addAction(BROADCAST_SYNC_ERROR);
        filter.addAction(BROADCAST_SYNC_CANCELLED);
        return filter;
    }
}
