package be.ugent.zeus.hydra.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.adapters.ActivityListAdapter;
import be.ugent.zeus.hydra.models.association.AssociationActivities;
import be.ugent.zeus.hydra.requests.AssociationActivitiesRequest;

/**
 * Created by ellen on 2016-03-08.
 */

public class ActivitiesFragment extends AbstractFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    private RecyclerView recyclerView;
    private ActivityListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private View layout;
    private StickyRecyclerHeadersDecoration decorator;

    @Override
    public void onResume() {
        super.onResume();
        this.sendScreenTracking("Activities");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_activities, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recyclerview);
        adapter = new ActivityListAdapter();
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        decorator = new StickyRecyclerHeadersDecoration((StickyRecyclerHeadersAdapter) adapter);
        recyclerView.addItemDecoration(decorator);
        performLoadActivityRequest();


        return layout;
    }


    private void performLoadActivityRequest() {

        final AssociationActivitiesRequest r = new AssociationActivitiesRequest();
        spiceManager.execute(r, r.getCacheKey(), r.getCacheDuration(), new RequestListener<AssociationActivities>() {



            @Override
            public void onRequestFailure(SpiceException spiceException) {
                showFailureSnackbar();
            }

            @Override
            public void onRequestSuccess(final AssociationActivities associationActivitiesItems) {
                System.out.println("------ updateeeee");
                adapter.setItems(associationActivitiesItems.getPreferedActivities(getContext()));
                adapter.notifyDataSetChanged();
            }
        });
    }



    private void showFailureSnackbar() {
        Snackbar
                .make(layout, "Oeps! Kon activiteiten niet ophalen.", Snackbar.LENGTH_LONG)
                .setAction("Opnieuw proberen", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        performLoadActivityRequest();
                    }
                })
                .show();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        System.out.println("------------- addaaaaapppttt");
        performLoadActivityRequest();
    }

    /*
    @Override
public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPrefs.registerOnSharedPreferenceChangeListener(this);
}

    @Override
    public void onStop() {
        spiceManager.shouldStop();

        super.onStop();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        sharedPrefs
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
sharedPrefs
        .unregisterOnSharedPreferenceChangeListener(this);
    }

    protected SpiceManager spiceManager = new SpiceManager(GsonSpringAndroidSpiceService.class);

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(getContext());
    }
*/


}
