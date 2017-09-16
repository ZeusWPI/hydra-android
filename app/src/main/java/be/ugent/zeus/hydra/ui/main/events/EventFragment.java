package be.ugent.zeus.hydra.ui.main.events;

import android.support.v4.app.Fragment;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.LinearLayout;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.repository.observers.AdapterObserver;
import be.ugent.zeus.hydra.repository.observers.ErrorObserver;
import be.ugent.zeus.hydra.repository.observers.ProgressObserver;
import be.ugent.zeus.hydra.ui.common.BaseActivity;
import be.ugent.zeus.hydra.ui.common.recyclerview.EmptyViewObserver;
import be.ugent.zeus.hydra.ui.preferences.SettingsActivity;

/**
 * Displays a list of activities, filtered by the settings.
 *
 * @author ellen
 * @author Niko Strijbol
 */
public class EventFragment extends Fragment {

    private static final String TAG = "EventFragment";

    private final EventAdapter adapter = new EventAdapter();
    private EventViewModel viewModel;

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

        LinearLayout noData = view.findViewById(R.id.events_no_data);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        //recyclerView.addItemDecoration(new StickyRecyclerHeadersDecoration(adapter));
        recyclerView.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new EmptyViewObserver(recyclerView, noData));

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.ugent_yellow_dark);

        viewModel = ViewModelProviders.of(this).get(EventViewModel.class);
        viewModel.getData().observe(this, ErrorObserver.with(this::onError));
        viewModel.getData().observe(this, new ProgressObserver<>(view.findViewById(R.id.progress_bar)));
        viewModel.getData().observe(this, new AdapterObserver<>(adapter));
        viewModel.getRefreshing().observe(this, swipeRefreshLayout::setRefreshing);
        swipeRefreshLayout.setOnRefreshListener(viewModel);

        Button refresh = view.findViewById(R.id.events_no_data_button_refresh);
        Button filters = view.findViewById(R.id.events_no_data_button_filters);

        refresh.setOnClickListener(v -> viewModel.onRefresh());
        filters.setOnClickListener(v -> startActivity(new Intent(getContext(), SettingsActivity.class)));
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(getView(), getString(R.string.failure), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.again), v -> viewModel.onRefresh())
                .show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main_events, menu);
        //TODO there must a better of doing this
        BaseActivity activity = (BaseActivity) getActivity();
        activity.tintToolbarIcons(menu, R.id.action_refresh);
        SearchView view = (SearchView) menu.findItem(R.id.action_search).getActionView();
        view.setOnQueryTextListener(adapter);
        view.setOnCloseListener(adapter);
        view.setOnSearchClickListener(v -> adapter.onOpen());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_refresh) {
            viewModel.onRefresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}