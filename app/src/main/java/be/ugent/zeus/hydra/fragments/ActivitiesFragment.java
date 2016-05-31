package be.ugent.zeus.hydra.fragments;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.SettingsActivity;
import be.ugent.zeus.hydra.adapters.ActivityListAdapter;
import be.ugent.zeus.hydra.common.DividerItemDecoration;
import be.ugent.zeus.hydra.common.RequestHandler;
import be.ugent.zeus.hydra.common.fragments.SpiceFragment;
import be.ugent.zeus.hydra.models.association.AssociationActivities;
import be.ugent.zeus.hydra.models.association.AssociationActivity;
import be.ugent.zeus.hydra.requests.AssociationActivitiesRequest;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.List;

import static be.ugent.zeus.hydra.common.ViewUtils.$;

/**
 * Displays a list of activities, filtered by the settings.
 *
 * @author ellen
 * @author Niko Strijbol
 */
public class ActivitiesFragment extends SpiceFragment implements RequestHandler.Requester<ArrayList<AssociationActivity>>, SharedPreferences.OnSharedPreferenceChangeListener{

    private ActivityListAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout noData;

    //If the data is invalidated.
    private boolean invalid = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activities, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = $(view, R.id.recycler_view);
        assert recyclerView != null;
        progressBar = $(view, R.id.progress_bar);
        noData = $(view, R.id.events_no_data);

        adapter = new ActivityListAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        StickyRecyclerHeadersDecoration decorator = new StickyRecyclerHeadersDecoration(adapter);
        recyclerView.addItemDecoration(decorator);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));

        performRequest(false);
        Button refresh = $(view, R.id.events_no_data_button_refresh);
        Button filters = $(view, R.id.events_no_data_button_filters);
        assert refresh != null && filters != null;

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
        filters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SettingsActivity.class));
            }
        });

        //Register this class in the settings.
        PreferenceManager.getDefaultSharedPreferences(getContext()).registerOnSharedPreferenceChangeListener(this);

        return layout;
    }

    /**
     * Hide the progress bar.
     */
    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Called when the requests has failed.
     */
    @Override
    public void requestFailure() {
        assert getView() != null;
        Snackbar.make(getView(), getString(R.string.failure), Snackbar.LENGTH_LONG)
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
        adapter.setOriginal(data);
        setData(data);
        hideProgressBar();
    }

    /**
     * Set the data. This assumes the data has been set once already. If the data is empty, a snackbar will be shown.
     *
     * @param data The data.
     */
    private void setData(List<AssociationActivity> data) {

        if(data == null) {
            requestFailure();
            return;
        }

        data = AssociationActivities.getPreferredActivities(data, getContext());

        adapter.setData(data);
        adapter.notifyDataSetChanged();

        //If empty, show it.
        if(data.isEmpty()) {
            noData.setVisibility(View.VISIBLE);
        } else {
            noData.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //Refresh the data.
        if(invalid) {
            //No need for a new thread, since this is pretty fast.
            adapter.setData(AssociationActivities.getPreferredActivities(adapter.getOriginal(), getContext()));
            adapter.notifyDataSetChanged();
            invalid = false;
        }
    }

    /**
     * Called when a shared preference is changed, added, or removed. This may be called even if a preference is set to
     * its existing value.
     * <p>
     * <p>This callback will be run on your main thread.
     *
     * @param sharedPreferences The {@link SharedPreferences} that received the change.
     * @param key               The key of the preference that was changed, added, or
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("pref_association_checkbox") || key.equals("associationPrefListScreen")) {
            invalid = true;
        }
    }
}