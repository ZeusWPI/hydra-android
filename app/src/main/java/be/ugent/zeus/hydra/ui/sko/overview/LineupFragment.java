package be.ugent.zeus.hydra.ui.sko.overview;

import android.support.v4.app.Fragment;
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
import be.ugent.zeus.hydra.sko.Artist;
import be.ugent.zeus.hydra.common.arch.observers.ErrorObserver;
import be.ugent.zeus.hydra.common.arch.observers.ProgressObserver;
import be.ugent.zeus.hydra.common.arch.observers.SuccessObserver;
import be.ugent.zeus.hydra.ui.common.BaseActivity;
import be.ugent.zeus.hydra.ui.common.recyclerview.TextCallback;
import su.j2e.rvjoiner.JoinableAdapter;
import su.j2e.rvjoiner.JoinableLayout;
import su.j2e.rvjoiner.RvJoiner;

import java.util.*;

/**
 * Show the lineup.
 *
 * @author Niko Strijbol
 */
public class LineupFragment extends Fragment {

    private static final String TAG = "LineupFragment";

    private RvJoiner joiner;
    private Map<String, LineupAdapter> adapters = new HashMap<>();
    private LineupViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sko_lineup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.sko_red);

        if (joiner == null) {
            joiner = new RvJoiner();
        }
        recyclerView.setAdapter(joiner.getAdapter());

        viewModel = ViewModelProviders.of(this).get(LineupViewModel.class);
        viewModel.getData().observe(this, ErrorObserver.with(this::onError));
        viewModel.getData().observe(this, new ProgressObserver<>(view.findViewById(R.id.progress_bar)));
        viewModel.getData().observe(this, SuccessObserver.with(this::receiveData));
        viewModel.getRefreshing().observe(this, refreshLayout::setRefreshing);
        refreshLayout.setOnRefreshListener(viewModel);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_refresh, menu);
        BaseActivity activity = (BaseActivity) getActivity();
        BaseActivity.tintToolbarIcons(activity.getToolbar(), menu, R.id.action_refresh);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            viewModel.onRefresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(getView(), getString(R.string.failure), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.again), v -> viewModel.onRefresh())
                .show();
    }
}