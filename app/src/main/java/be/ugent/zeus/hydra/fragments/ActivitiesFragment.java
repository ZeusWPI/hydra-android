package be.ugent.zeus.hydra.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.adapters.ActivityListAdapter;
import be.ugent.zeus.hydra.common.DividerItemDecoration;
import be.ugent.zeus.hydra.common.RequestHandler;
import be.ugent.zeus.hydra.common.fragments.SpiceFragment;
import be.ugent.zeus.hydra.models.association.AssociationActivities;
import be.ugent.zeus.hydra.models.association.AssociationActivity;
import be.ugent.zeus.hydra.requests.AssociationActivitiesRequest;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;

/**
 * Displays a list of activities, filtered by the settings.
 *
 * @author ellen
 * @author Niko Strijbol
 *
 * TODO: update after  settings changed.
 */
public class ActivitiesFragment extends SpiceFragment implements RequestHandler.Requester<ArrayList<AssociationActivity>>{

    private ActivityListAdapter adapter;
    private View layout;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_activities, container, false);
        RecyclerView recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
        progressBar = (ProgressBar) layout.findViewById(R.id.progress_bar);

        adapter = new ActivityListAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        StickyRecyclerHeadersDecoration decorator = new StickyRecyclerHeadersDecoration(adapter);
        recyclerView.addItemDecoration(decorator);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));

        performRequest(false);

        return layout;
    }

    /**
     * Hide the progress bar.
     */
    private void hideProgressBar() {
        if(progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Called when the requests has failed.
     */
    @Override
    public void requestFailure() {
        Snackbar.make(layout, getString(R.string.failure), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.again), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        performRequest(true);
                    }
                })
                .show();
        hideProgressBar();
    }

    @Override
    public void performRequest(boolean refresh) {
        RequestHandler.performListRequest(refresh, new AssociationActivitiesRequest(), this);
    }

    /**
     * Called when the request was able to produce data.
     *
     * @param data The data.
     */
    @Override
    public void receiveData(ArrayList<AssociationActivity> data) {
        adapter.setData(AssociationActivities.getPreferredActivities(data, getContext()));
        adapter.notifyDataSetChanged();
        hideProgressBar();
    }
}