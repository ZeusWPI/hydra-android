package be.ugent.zeus.hydra.ui.main.events;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.LinearLayout;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.repository.RefreshBroadcast;
import be.ugent.zeus.hydra.repository.observers.AdapterObserver;
import be.ugent.zeus.hydra.repository.observers.ProgressObserver;
import be.ugent.zeus.hydra.repository.utils.ErrorUtils;
import be.ugent.zeus.hydra.ui.common.BaseActivity;
import be.ugent.zeus.hydra.ui.preferences.SettingsActivity;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import static be.ugent.zeus.hydra.ui.common.ViewUtils.$;

/**
 * Displays a list of activities, filtered by the settings.
 *
 * @author ellen
 * @author Niko Strijbol
 */
public class EventFragment extends LifecycleFragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "EventFragment";

    private final EventAdapter adapter = new EventAdapter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activities, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout noData = $(view, R.id.events_no_data);

        RecyclerView recyclerView = $(view, R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new StickyRecyclerHeadersDecoration(adapter));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        SwipeRefreshLayout swipeRefreshLayout = $(view, R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.ugent_yellow_dark);
        swipeRefreshLayout.setOnRefreshListener(this);

        EventViewModel model = ViewModelProviders.of(this).get(EventViewModel.class);
        ErrorUtils.filterErrors(model.getData()).observe(this, this::onError);
        model.getData().observe(this, new ProgressObserver<>($(view, R.id.progress_bar)));
        model.getData().observe(this, new AdapterObserver<>(adapter));
        model.getData().observe(this, listResult -> {
            if (listResult == null || (listResult.hasData() && listResult.getData().isEmpty())) {
                noData.setVisibility(View.VISIBLE);
            } else {
                noData.setVisibility(View.GONE);
            }
        });
        model.getRefreshing().observe(this, swipeRefreshLayout::setRefreshing);

        Button refresh = $(view, R.id.events_no_data_button_refresh);
        Button filters = $(view, R.id.events_no_data_button_filters);

        refresh.setOnClickListener(v -> onRefresh());
        filters.setOnClickListener(v -> startActivity(new Intent(getContext(), SettingsActivity.class)));
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(getView(), getString(R.string.failure), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.again), v -> onRefresh())
                .show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_refresh, menu);
        //TODO there must a better of doing this
        BaseActivity activity = (BaseActivity) getActivity();
        BaseActivity.tintToolbarIcons(activity.getToolbar(), menu, R.id.action_refresh);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_refresh) {
            onRefresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        RefreshBroadcast.broadcastRefresh(getContext(), true);
    }
}