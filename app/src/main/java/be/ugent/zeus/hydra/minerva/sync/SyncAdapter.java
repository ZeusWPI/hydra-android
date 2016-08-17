package be.ugent.zeus.hydra.minerva.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;

import be.ugent.zeus.hydra.cache.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.minerva.announcement.AnnouncementDao;
import be.ugent.zeus.hydra.minerva.course.CourseDao;
import be.ugent.zeus.hydra.minerva.announcement.AnnouncementNotificationBuilder;
import be.ugent.zeus.hydra.models.minerva.Announcement;
import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.models.minerva.Courses;
import be.ugent.zeus.hydra.models.minerva.WhatsNew;
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

    public static final String FIRST_SYNC = "firstSync";

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

        if(cancelled) {
            return;
        }

        final boolean first = extras.getBoolean(FIRST_SYNC, false);

        CoursesMinervaRequest request = new CoursesMinervaRequest(getContext(), account, null);

        try {
            Log.d(TAG, "Performing request");
            Courses courses = request.performRequest();

            CourseDao dao = new CourseDao(getContext());
            AnnouncementDao announcementDao = new AnnouncementDao(getContext());
            dao.synchronise(courses.getCourses());
            Log.d(TAG, "Sync courses done.");

            //Add announcements
            for(Course course: courses.getCourses()) {
                if(cancelled) {
                    return;
                }
                //We don't add the course to announcements here, since that would be inefficient.
                Log.d(TAG, "Syncing course " + course.getId());
                WhatsNewRequest newRequest = new WhatsNewRequest(course, getContext(), null);
                WhatsNew w = newRequest.performRequest();
                Collection<Announcement> newOnes = announcementDao.synchronisePartial(w.getAnnouncements(), course, first);
                if(!first) {
                    notifyUser(newOnes);
                }
            }

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
        } catch (SQLException e) {
            syncResult.databaseError = true;
            Log.w(TAG, "Sync error.", e);
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
}
