package be.ugent.zeus.hydra.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import be.ugent.android.sdk.oauth.AuthorizationManager;
import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.AuthenticationActivity;
import be.ugent.zeus.hydra.fragments.common.LoaderFragment;
import be.ugent.zeus.hydra.loader.cache.CacheRequest;
import be.ugent.zeus.hydra.loader.cache.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.loader.requests.RequestExecutor;
import be.ugent.zeus.hydra.models.minerva.Announcement;
import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.models.minerva.Courses;
import be.ugent.zeus.hydra.models.minerva.WhatsNew;
import be.ugent.zeus.hydra.recyclerview.adapters.CourseAdapter;
import be.ugent.zeus.hydra.requests.CoursesMinervaRequest;
import be.ugent.zeus.hydra.requests.WhatsNewRequest;
import be.ugent.zeus.hydra.utils.DividerItemDecoration;

import static android.app.Activity.RESULT_OK;
import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Created by silox on 17/10/15.
 */

public class MinervaFragment extends LoaderFragment<Courses> {

    private static final String TAG = "MinervaFragment";

    private static final int AUTH_REQUEST = 1;

    private Button refresh;
    private RecyclerView recyclerView;
    private View authWrapper;

    private AuthorizationManager authorizationManager;
    private CourseAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authorizationManager = ((HydraApplication)getActivity().getApplication()).getAuthorizationManager();
    }

    /**
     * Do not automatically start the loaders, we do it by hand.
     */
    public MinervaFragment() {
        autoStart = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_minerva, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hideProgressBar();

       // refresh = $(view, R.id.refresh);
//        refresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                authorizationManager.invalidateToken();
//                restartLoader();
//            }
//        });

        Button authorize = $(view, R.id.authorize);
        authorize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maybeLaunchAuthorization();
            }
        });

        authWrapper = $(view, R.id.auth_wrapper);

        recyclerView = $(view, R.id.recycler_view);
        adapter = new CourseAdapter();

        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private boolean isLoggedIn() {
        return authorizationManager.isAuthenticated();
    }

    private void maybeLaunchAuthorization() {
        if (!isLoggedIn()) {
            Intent intent = new Intent(getContext(), AuthenticationActivity.class);
            startActivityForResult(intent, AUTH_REQUEST);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //We received data
        if(requestCode == AUTH_REQUEST) {
            //Was it ok?
            if(resultCode == RESULT_OK) {
                // Attempt to load data
                maybeLoadData();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Thanks to the constructor the loaders are not started in the parent function. We start them here
     * manually.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        maybeLoadData();
    }

    /**
     * Check if we can load data and if yes, do it.
     */
    private void maybeLoadData() {
        //If we are logged in, we can start loading the data.
        if(isLoggedIn()) {
            authWrapper.setVisibility(View.GONE);
            showProgressBar();
            startLoader();
        }
    }

    public void requestCourseAnnouncements(final Course course) {
        WhatsNewRequest whatsNewRequest = new WhatsNewRequest(course, (HydraApplication) getActivity().getApplication());

        RequestExecutor.executeAsync(whatsNewRequest, new RequestExecutor.Callback<WhatsNew>() {
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
        recyclerView.setVisibility(View.VISIBLE);
        System.out.println("Minerva Courses: ");
        adapter.setItems(data.getCourses());
        for (final Course course: data.getCourses()) {
            System.out.println("\t" + course.getTitle() + "\t" + course.getTutorName());
            //requestCourseAnnouncements(course);
        }
    }

    /**
     * @return The request that will be executed.
     */
    @Override
    public CacheRequest<Courses> getRequest() {
        return new CoursesMinervaRequest((HydraApplication) getActivity().getApplication());
    }
}