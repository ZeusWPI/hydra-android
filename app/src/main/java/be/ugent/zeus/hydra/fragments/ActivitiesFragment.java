package be.ugent.zeus.hydra.fragments;

import android.content.Intent;
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
import be.ugent.zeus.hydra.models.association.AssociationActivities;
import be.ugent.zeus.hydra.models.association.AssociationActivity;
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
                ArrayList<String> listItems = new ArrayList<String>();
                ArrayAdapter<String> adapter;
                final ListView activityList = (ListView) getView().findViewById(R.id.activityList);
                adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, listItems);
                activityList.setAdapter(adapter);

                activityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        int itemPosition = position;
                        Intent intent = new Intent(getContext(), AssociationActivityDetail.class);
                        intent.putExtra("associationActivity", associationActivitiesItems.get(itemPosition));
                        startActivity(intent);
                        //Toast.makeText(getContext(),
                        //        "Position :" + itemPosition + "  ListItem : " + associationActivitiesItems.get(itemPosition).title, Toast.LENGTH_LONG)
                        //        .show();
                    }
                });

                for (AssociationActivity activity : associationActivitiesItems) {
                    listItems.add(activity.title);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}
