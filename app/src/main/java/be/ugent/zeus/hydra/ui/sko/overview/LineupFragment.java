package be.ugent.zeus.hydra.ui.sko.overview;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.sko.Artist;
import be.ugent.zeus.hydra.repository.RefreshBroadcast;
import be.ugent.zeus.hydra.repository.observers.ProgressObserver;
import be.ugent.zeus.hydra.repository.observers.SuccessObserver;
import be.ugent.zeus.hydra.repository.utils.ErrorUtils;
import be.ugent.zeus.hydra.ui.common.BaseActivity;
import be.ugent.zeus.hydra.ui.common.recyclerview.TextCallback;
import su.j2e.rvjoiner.JoinableAdapter;
import su.j2e.rvjoiner.JoinableLayout;
import su.j2e.rvjoiner.RvJoiner;

import java.util.*;

import static be.ugent.zeus.hydra.ui.common.ViewUtils.$;

/**
 * Show the lineup.
 *
 * @author Niko Strijbol
 */
public class LineupFragment extends LifecycleFragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "LineupFragment";

    private RvJoiner joiner;
    private Map<String, LineupAdapter> adapters = new HashMap<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sko_lineup, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = $(view, R.id.recycler_view);

        SwipeRefreshLayout refreshLayout = $(view, R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.sko_red);
        refreshLayout.setOnRefreshListener(this);

        if (joiner == null) {
            joiner = new RvJoiner();
        }
        recyclerView.setAdapter(joiner.getAdapter());

        LineupViewModel model = ViewModelProviders.of(this).get(LineupViewModel.class);
        ErrorUtils.filterErrors(model.getData()).observe(this, this::onError);
        model.getData().observe(this, new ProgressObserver<>($(view, R.id.progress_bar)));
        model.getData().observe(this, SuccessObserver.with(this::receiveData));
        model.getRefreshing().observe(this, refreshLayout::setRefreshing);
    }

    private void receiveData(@NonNull Collection<Artist> data) {
        //Sort into stages
        Map<String, List<Artist>> stages = new LinkedHashMap<>();

        for (Artist artist : data) {
            if (!stages.containsKey(artist.getStage())) {
                stages.put(artist.getStage(), new ArrayList<>());
            }
            stages.get(artist.getStage()).add(artist);
        }

        for (Map.Entry<String, List<Artist>> entry : stages.entrySet()) {
            if (!adapters.containsKey(entry.getKey())) {
                LineupAdapter adapter = new LineupAdapter();
                adapters.put(entry.getKey(), adapter);
                joiner.add(new JoinableLayout(R.layout.item_title, new TextCallback(entry.getKey())));
                joiner.add(new JoinableAdapter(adapter));
            }
            adapters.get(entry.getKey()).setItems(entry.getValue());
        }
    }

    @Override
    public void onRefresh() {
        RefreshBroadcast.broadcastRefresh(getContext(), true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_refresh, menu);
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

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(getView(), getString(R.string.failure), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.again), v -> onRefresh())
                .show();
    }
}