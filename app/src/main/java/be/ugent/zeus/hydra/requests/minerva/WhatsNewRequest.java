package be.ugent.zeus.hydra.requests.minerva;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;

import be.ugent.zeus.hydra.cache.Cache;
import be.ugent.zeus.hydra.requests.common.RequestFailureException;
import be.ugent.zeus.hydra.cache.file.SerializeCache;
import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.models.minerva.Courses;
import be.ugent.zeus.hydra.models.minerva.WhatsNew;

import static be.ugent.zeus.hydra.cache.Cache.ONE_HOUR;

/**
 * Created by feliciaan on 29/06/16.
 */
public class WhatsNewRequest extends MinervaRequest<WhatsNew> {

    private static final String TAG = "WhatsNewRequest";

    public static final String BASE_KEY = "whatsnewRequest";

    private Course course;

    public WhatsNewRequest(Course course, Context context, Activity activity) {
        super(WhatsNew.class, context, activity);
        this.course = course;
    }

    @Override
    @NonNull
    public String getCacheKey() {
        return BASE_KEY + "." + course.getId();
    }

    @Override
    @NonNull
    protected String getAPIUrl() {
        return MINERVA_API + "course/" + course.getId() + "/whatsnew";
    }

    @Override
    public long getCacheDuration() {
        return ONE_HOUR;
    }

    /**
     * Get announcements for all courses. While the initial request for the courses is executed in sync, the requests
     * for the courses are not. Every time new announcements are loaded, they are passed to the listener.
     */
    public static void getAllAnnouncements(final Courses courses, final Context context, final Activity activity, final AnnouncementsListener listener) {

        final Cache cache = new SerializeCache(context.getApplicationContext());

        AsyncTask<Void, Pair<Course, WhatsNew>, Void> task = new AsyncTask<Void, Pair<Course, WhatsNew>, Void>() {

            private Throwable error;

            @Override
            protected Void doInBackground(Void... voids) {

                try {
                    for (Course course : courses.getCourses()) {
                        //noinspection unchecked
                        publishProgress(new Pair<>(course, cache.get(new WhatsNewRequest(course, context, activity))));
                    }
                } catch (RequestFailureException e) {
                    error = e;
                }

                return null;
            }

            @SafeVarargs
            @Override
            protected final void onProgressUpdate(Pair<Course, WhatsNew>... values) {
                Pair<Course, WhatsNew> p = values[0];
                listener.onAnnouncementsAdded(p.second, p.first);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(error != null) {
                    Log.d(TAG, "An error occurred.", error);
                    listener.error(error);
                } else {
                    listener.completed();
                }
            }
        };

        task.execute();
    }

    public interface AnnouncementsListener {

        /**
         * Called when announcements are added to the list.
         */
        void onAnnouncementsAdded(WhatsNew whatsNew, Course course);

        void completed();

        void error(Throwable lastError);
    }
}