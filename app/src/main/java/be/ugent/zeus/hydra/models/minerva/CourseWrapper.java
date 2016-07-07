package be.ugent.zeus.hydra.models.minerva;

import android.support.annotation.NonNull;
import android.util.Log;
import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.loader.cache.Cache;
import be.ugent.zeus.hydra.loader.cache.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.loader.cache.file.SerializeCache;
import be.ugent.zeus.hydra.loader.requests.Request;
import be.ugent.zeus.hydra.loader.requests.RequestExecutor;
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

    private Course course;
    private List<Announcement> announcements = Collections.emptyList();
    private HydraApplication application;
    private Cache cache;

    public CourseWrapper(Course c, HydraApplication application) {
        this.course = c;
        this.application = application;
        this.cache = new SerializeCache(application.getApplicationContext());
    }

    public void loadAnnouncements() {

        //Request
        final WhatsNewRequest whatsNewRequest = new WhatsNewRequest(course, application);

        //Wrap in request for cache
        Request<WhatsNew> request = new Request<WhatsNew>() {
            @NonNull
            @Override
            public WhatsNew performRequest() throws RequestFailureException {
                return cache.get(whatsNewRequest);
            }
        };

        //Do request
        RequestExecutor.executeAsync(request, new RequestExecutor.Callback<WhatsNew>() {
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
            }

            @Override
            public void receiveError(RequestFailureException e) {
                Log.e("CourseAnnouncement", "Error while getting announcements", e);
            }
        });
    }

    public Course getCourse() {
        return course;
    }

    public List<Announcement> getAnnouncements() {
        return announcements;
    }
}