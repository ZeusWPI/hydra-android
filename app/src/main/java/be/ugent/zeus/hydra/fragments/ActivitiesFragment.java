package be.ugent.zeus.hydra.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.adapters.ActivityListAdapter;
import be.ugent.zeus.hydra.models.association.Activities;
import be.ugent.zeus.hydra.requests.AssociationActivitiesRequest;
/**
 * Created by ellen on 2016-03-08.
 *
 * TODO: update after  settings changed.
 */

public class ActivitiesFragment extends AbstractFragment {
    private RecyclerView recyclerView;
    private ActivityListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private View layout;
    private StickyRecyclerHeadersDecoration decorator;
    private ProgressBar progressBar;

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
        progressBar = (ProgressBar) layout.findViewById(R.id.progressBar);

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
        spiceManager.execute(r, r.getCacheKey(), r.getCacheDuration(), new RequestListener<Activities>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                showFailureSnackbar();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onRequestSuccess(final Activities activitiesItems) {
                adapter.setItems(activitiesItems.getPreferedActivities(getContext()));
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
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
    public void onPause() {
        super.onPause();
        performLoadActivityRequest();
    }

}
