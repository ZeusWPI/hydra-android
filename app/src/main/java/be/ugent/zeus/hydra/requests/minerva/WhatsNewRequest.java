package be.ugent.zeus.hydra.requests.minerva;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;

import be.ugent.zeus.hydra.loader.cache.Cache;
import be.ugent.zeus.hydra.loader.cache.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.loader.cache.file.SerializeCache;
import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.models.minerva.Courses;
import be.ugent.zeus.hydra.models.minerva.WhatsNew;

import static be.ugent.zeus.hydra.loader.cache.Cache.ONE_HOUR;

/**
 * Created by feliciaan on 29/06/16.
 */
public class WhatsNewRequest extends MinervaRequestTwo<WhatsNew> {

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
     * @return
     */
    public static void getAllAnnouncements(final Courses courses, final Context context, final Activity activity, final AnnouncementsListener listener) {

        final Cache cache = new SerializeCache(context.getApplicationContext());

        AsyncTask<Void, Pair<Course, WhatsNew>, Void> task = new AsyncTask<Void, Pair<Course, WhatsNew>, Void>() {

            private boolean error = false;

            @Override
            protected Void doInBackground(Void... voids) {
                Log.d("Background", "Adding courses");
                for(Course course : courses.getCourses()) {
                    try {
                        //listener.onAnnouncementsAdded(w, course);
                        //noinspection unchecked
                        publishProgress(new Pair<>(course, cache.get(new WhatsNewRequest(course, context, activity))));
                    } catch (RequestFailureException e) {
                        error = true;
                        Log.d("WhatsNewRequest", "Course what's new failed: " + course.getTitle());
                    }
                }

                return null;
            }

            @SafeVarargs
            @Override
            protected final void onProgressUpdate(Pair<Course, WhatsNew>... values) {
                Pair<Course, WhatsNew> p = values[0];
                Log.d("Progress", "Progress for " + p.first.getTitle());
                listener.onAnnouncementsAdded(p.second, p.first);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(error) {
                    listener.error();
                } else {
                    listener.completed();
                }
            }
        };

        Log.d("All announcements", "Requesting everything!");
        task.execute();
    }

    public interface AnnouncementsListener {

        /**
         * Called when announcements are added to the list.
         */
        void onAnnouncementsAdded(WhatsNew whatsNew, Course course);

        void completed();

        void error();
    }
}