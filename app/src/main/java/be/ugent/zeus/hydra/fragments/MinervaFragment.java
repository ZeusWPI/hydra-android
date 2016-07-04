package be.ugent.zeus.hydra.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import be.ugent.android.sdk.oauth.AuthorizationManager;
import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.AuthenticationActivity;
import be.ugent.zeus.hydra.fragments.common.LoaderFragment;
import be.ugent.zeus.hydra.loader.cache.Request;
import be.ugent.zeus.hydra.loader.cache.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.loader.tasks.NetworkTaskExecutor;
import be.ugent.zeus.hydra.models.minerva.Announcement;
import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.models.minerva.Courses;
import be.ugent.zeus.hydra.models.minerva.WhatsNew;
import be.ugent.zeus.hydra.requests.CoursesMinervaRequest;
import be.ugent.zeus.hydra.requests.WhatsNewRequest;

/**
 * Created by silox on 17/10/15.
 */

public class MinervaFragment extends LoaderFragment<Courses> {

    private View layout;
    private Button refresh;
    private Button login;

    private AuthorizationManager authorizationManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authorizationManager = ((HydraApplication)getActivity().getApplication()).getAuthorizationManager();
    }

    public MinervaFragment() {
        autoStart = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_minerva, container, false);

        refresh = (Button) layout.findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authorizationManager.invalidateToken();
                restartLoader();
            }
        });

        login = (Button) layout.findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AuthenticationActivity.class);
                getContext().startActivity(intent);
            }
        });

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!authorizationManager.hasValidToken() || !authorizationManager.isAuthenticated()) {
            Intent intent = new Intent(getContext(), AuthenticationActivity.class);
            getContext().startActivity(intent);
        }

        startLoader();
    }

    public void requestCourseAnnouncements(final Course course) {
        WhatsNewRequest whatsNewRequest = new WhatsNewRequest(course, (HydraApplication) getActivity().getApplication());

        NetworkTaskExecutor.executeAsync(whatsNewRequest, new NetworkTaskExecutor.NetworkCallBack<WhatsNew>() {
            @Override
            public void receiveData(@NonNull WhatsNew data) {
                System.out.println("WhatsNew for " + course.getTitle());
                for (Announcement announcement: data.getAnnouncements()) {
                    announcement.setCourse(course);
                    System.out.println("Announcement: " + announcement.getTitle() + "   " + announcement.getLecturer());
                }
            }

            @Override
            public void receiveError(RequestFailureException e) {
                System.out.println("Minerva courses whatsnew execption " + e.getLocalizedMessage());
            }
        });
    }

    /**
     * This must be called when data is received that has no errors.
     *
     * @param data The data.
     */
    @Override
    public void receiveData(@NonNull Courses data) {
        System.out.println("Minerva Courses: ");
        for (final Course course: data.getCourses()) {
            System.out.println("\t" + course.getTitle() + "\t" + course.getTutorName());
            requestCourseAnnouncements(course);
        }
    }

    /**
     * @return The request that will be executed.
     */
    @Override
    public Request<Courses> getRequest() {
        return new CoursesMinervaRequest((HydraApplication) getActivity().getApplication());
    }
}
