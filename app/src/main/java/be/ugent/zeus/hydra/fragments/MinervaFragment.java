package be.ugent.zeus.hydra.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import be.ugent.android.sdk.oauth.json.GrantInformation;
import be.ugent.android.sdk.oauth.request.GrantInformationRequest;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.AuthenticationActivity;
import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.models.minerva.Courses;
import be.ugent.zeus.hydra.requests.CoursesMinervaRequest;
import be.ugent.zeus.hydra.requests.UserInfoRequest;
import roboguice.inject.InjectView;

/**
 * Created by silox on 17/10/15.
 */

public class MinervaFragment extends AbstractFragment {
    private View layout;
    private Button refresh;
    private Button login;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_minerva, container, false);

        refresh = (Button) layout.findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestCourses();
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
        this.sendScreenTracking("Minerva");
        if (!authorizationManager.hasValidToken() || !authorizationManager.isAuthenticated()) {
            Intent intent = new Intent(getContext(), AuthenticationActivity.class);
            getContext().startActivity(intent);
        }

        requestCourses();
    }

    public void requestCourses() {
        CoursesMinervaRequest r = new CoursesMinervaRequest(getContext().getApplicationContext());
        r.execute(minervaSpiceManager, new RequestListener<Courses>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                System.out.println("Minerva courses execption " + spiceException.getLocalizedMessage());
            }

            @Override
            public void onRequestSuccess(Courses courses) {
                System.out.println("Minerva Courses: ");
                for (Course course: courses.getCourses()) {
                    System.out.println("\t" + course.getTitle() + "\t" + course.getTutorName());
                }
            }
        });

        requestUserInformation();
    }


    private void requestUserInformation() {
        getActivity().setProgressBarIndeterminateVisibility(true);
        UserInfoRequest userInfoRequest = new UserInfoRequest(getContext().getApplicationContext());

    }

    private class GrantInformationRequestListener implements RequestListener<GrantInformation> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            // The HTTP Response was not 200 or there was a problem
            // parsing the response to the `GrantInformation` object.
            getActivity().setProgressBarIndeterminateVisibility(false);
            System.out.println("Request Failed.");
        }

        @Override
        public void onRequestSuccess(GrantInformation grantInformation) {
            // The request was successful and a `GrantInformation` POJO
            // representation of the response data is sent along.
            getActivity().setProgressBarIndeterminateVisibility(false);
            System.out.println(grantInformation.userAttributes.getFullName());
        }

    }
}
