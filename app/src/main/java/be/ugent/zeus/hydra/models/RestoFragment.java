package be.ugent.zeus.hydra.models;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.octo.android.robospice.GsonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.AssociationActivityDetail;
import be.ugent.zeus.hydra.adapters.ActivityListAdapter;
import be.ugent.zeus.hydra.models.Association.AssociationActivities;
import be.ugent.zeus.hydra.models.Association.AssociationActivity;
import be.ugent.zeus.hydra.requests.AssociationActivitiesRequest;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RestoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RestoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RestoFragment extends Fragment {
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
        View view = inflater.inflate(R.layout.fragment_resto, container, false);

        performLoadActivityRequest();

        return view;
    }

    private void performLoadActivityRequest() {
        final AssociationActivitiesRequest r = new AssociationActivitiesRequest();
        spiceManager.execute(r, r.getCacheKey(), r.getCacheDuration(), new RequestListener<AssociationActivities>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                System.out.println("Request failed");
            }

            @Override
            public void onRequestSuccess(final AssociationActivities associationActivitiesItems) {

            }
        });
    }
}
