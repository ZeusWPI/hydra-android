package be.ugent.zeus.hydra.models.minerva;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import be.ugent.zeus.hydra.minerva.announcement.AnnouncementDao;
import be.ugent.zeus.hydra.requests.common.Request;
import be.ugent.zeus.hydra.requests.common.RequestFailureException;
import be.ugent.zeus.hydra.requests.executor.RequestCallback;
import be.ugent.zeus.hydra.requests.executor.RequestExecutor;

import java.util.*;

/**
 * Wrapper for a course and announcements.
 *
 * @author Niko Strijbol
 */
public class CourseWrapper {

    private static final String TAG = "CourseWrapper";

    private Course course;
    private List<Announcement> announcements = Collections.emptyList();
    private Context context;

    private AsyncTask task;

    public CourseWrapper(Course c, Context context) {
        this.course = c;
        this.context = context;
    }

    /**
     * Cancel the task if it exists.
     */
    public void cancelLoading() {
        if(task != null) {
            Log.d(TAG, "Canceled request for " + course);
            task.cancel(false);
            task = null;
        }
    }

    /**
     * Load the announcements for this course.
     *
     * @param callback The callback.
     */
    public void loadAnnouncements(final RequestCallback<List<Announcement>> callback) {

        //It is already loaded.
        if(!announcements.isEmpty()) {
            callback.receiveData(announcements);
            return;
        }

        //Make request
        Request<List<Announcement>> request = new Request<List<Announcement>>() {
            @NonNull
            @Override
            public List<Announcement> performRequest() throws RequestFailureException {
                //Get dao
                AnnouncementDao dao = new AnnouncementDao(context);
                return dao.getAnnouncementsForCourse(course);
            }
        };

        //Do request
        task = RequestExecutor.executeAsync(request, new RequestCallback<List<Announcement>>() {
            @Override
            public void receiveData(@NonNull List<Announcement> data) {
                Collections.reverse(data);
                announcements = data;
                Log.d(TAG, "got announcements!: " + data.size());
                task = null;
                callback.receiveData(data);
            }

            @Override
            public void receiveError(@NonNull Throwable e) {
                Log.e(TAG, "Error while getting announcements", e);
                task = null;
                callback.receiveError(e);
            }
        });
    }

    /**
     * @return The course.
     */
    public Course getCourse() {
        return course;
    }

    /**
     * @return The announcements.
     */
    public List<Announcement> getAnnouncements() {
        return announcements;
    }
}