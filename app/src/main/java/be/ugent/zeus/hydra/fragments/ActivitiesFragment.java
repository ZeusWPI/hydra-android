package be.ugent.zeus.hydra.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.octo.android.robospice.GsonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.Association.AssociationActivities;
import be.ugent.zeus.hydra.models.Association.AssociationActivity;
import be.ugent.zeus.hydra.requests.AssociationActivitiesRequest;

/**
 * A simple {@link Fragment} subclass.
 * ActivitiesFragment that contain this fragment must implement the
 * {@link ActivitiesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ActivitiesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActivitiesFragment extends Fragment {

    protected SpiceManager spiceManager = new SpiceManager(GsonSpringAndroidSpiceService.class);

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(getContext());
    }

    @Override
    public void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_activities, container, false);
        TextView activitytext = (TextView) view.findViewById(R.id.activity);
        activitytext.setText("hello activities");

        performLoadActivityRequest();

        return view;
    }

    private void performLoadActivityRequest() {

        final AssociationActivitiesRequest r = new AssociationActivitiesRequest();
        spiceManager.execute(r, r.getCacheKey(), r.getCacheDuration(), new RequestListener<AssociationActivities>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                TextView activitytext = (TextView) getView().findViewById(R.id.activity);
                activitytext.setText(r.getCacheKey() + " " + r.getCacheDuration() + " \n" + spiceException.toString());
            }

            @Override
            public void onRequestSuccess(AssociationActivities associationActivitiesItems) {
                for(AssociationActivity activity: associationActivitiesItems) {
                    TextView activitytext = (TextView) getView().findViewById(R.id.activity);
                    activitytext.setText(activity.title);
                }
            }
        });
    }
}
