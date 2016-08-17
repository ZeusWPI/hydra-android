package be.ugent.zeus.hydra.models.minerva;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import be.ugent.zeus.hydra.cache.Cache;
import be.ugent.zeus.hydra.cache.file.SerializeCache;
import be.ugent.zeus.hydra.requests.common.Request;
import be.ugent.zeus.hydra.requests.common.RequestFailureException;
import be.ugent.zeus.hydra.requests.executor.RequestCallback;
import be.ugent.zeus.hydra.requests.executor.RequestExecutor;
import be.ugent.zeus.hydra.requests.minerva.WhatsNewRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

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
    private Cache cache;

    private AsyncTask task;

    public CourseWrapper(Course c, Context context) {
        this.course = c;
        this.context = context;
        this.cache = new SerializeCache(context.getApplicationContext());
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

        //Request
        final WhatsNewRequest whatsNewRequest = new WhatsNewRequest(course, context, null);

        //Wrap in request for cache
        Request<WhatsNew> request = new Request<WhatsNew>() {
            @NonNull
            @Override
            public WhatsNew performRequest() throws RequestFailureException {
                return cache.get(whatsNewRequest);
            }
        };

        //Do request
        task = RequestExecutor.executeAsync(request, new RequestCallback<WhatsNew>() {
            @Override
            public void receiveData(@NonNull WhatsNew data) {
                ListIterator<Announcement> li = data.getAnnouncements().listIterator(data.getAnnouncements().size());
                announcements = new ArrayList<>();
                // Iterate in reverse.
                while (li.hasPrevious()) {
                    Announcement a = li.previous();
                    a.setCourse(course);
                    announcements.add(a);
                }
                task = null;
                callback.receiveData(data.getAnnouncements());
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