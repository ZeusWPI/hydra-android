package be.ugent.zeus.hydra.association.event.list;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.arch.observers.AdapterObserver;
import be.ugent.zeus.hydra.common.arch.observers.PartialErrorObserver;
import be.ugent.zeus.hydra.common.arch.observers.ProgressObserver;
import be.ugent.zeus.hydra.preferences.PreferenceActivity;
import be.ugent.zeus.hydra.preferences.PreferenceEntry;
import com.google.android.material.snackbar.Snackbar;

import static be.ugent.zeus.hydra.utils.FragmentUtils.requireBaseActivity;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activities, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout noData = view.findViewById(R.id.events_no_data);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        // TODO
        // adapter.registerAdapterDataObserver(new EmptyViewObserver(recyclerView, noData));

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.hydra_secondary_colour);

        viewModel = ViewModelProviders.of(this).get(EventViewModel.class);
        viewModel.getData().observe(this, PartialErrorObserver.with(this::onError));
        viewModel.getData().observe(this, new ProgressObserver<>(view.findViewById(R.id.progress_bar)));
        viewModel.getData().observe(this, new AdapterObserver<>(adapter));
        viewModel.getRefreshing().observe(this, swipeRefreshLayout::setRefreshing);
        swipeRefreshLayout.setOnRefreshListener(viewModel);

        Button refresh = view.findViewById(R.id.events_no_data_button_refresh);
        Button filters = view.findViewById(R.id.events_no_data_button_filters);

        refresh.setOnClickListener(v -> viewModel.onRefresh());
        filters.setOnClickListener(v -> PreferenceActivity.start(requireContext(), PreferenceEntry.ACTIVITIES));
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(requireView(), getString(R.string.error_network), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.action_again), v -> viewModel.onRefresh())
                .show();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main_events, menu);
        requireBaseActivity(this).tintToolbarIcons(menu, R.id.action_refresh);
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
