package be.ugent.zeus.hydra.ui.main.library;

import android.support.v4.app.Fragment;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.*;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.repository.observers.AdapterObserver;
import be.ugent.zeus.hydra.repository.observers.ErrorObserver;
import be.ugent.zeus.hydra.repository.observers.ProgressObserver;
import be.ugent.zeus.hydra.ui.common.BaseActivity;
import be.ugent.zeus.hydra.ui.common.recyclerview.EmptyViewObserver;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

/**
 * @author Niko Strijbol
 */
public class LibraryListFragment extends Fragment {

    private static final String TAG = "LibraryListFragment";
    private static final String LIB_URL = "http://lib.ugent.be/";
    public static final String PREF_LIBRARY_FAVOURITES = "pref_library_favourites";

    private final LibraryListAdapter adapter = new LibraryListAdapter();
    private LibraryViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        RecyclerFastScroller s = view.findViewById(R.id.fast_scroller);
        s.attachRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new EmptyViewObserver(recyclerView, view.findViewById(R.id.no_data_view)));

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.ugent_yellow_dark);

        viewModel = ViewModelProviders.of(this).get(LibraryViewModel.class);
        viewModel.getData().observe(this, ErrorObserver.with(this::onError));
        viewModel.getData().observe(this, new ProgressObserver<>(view.findViewById(R.id.progress_bar)));
        viewModel.getData().observe(this, new AdapterObserver<>(adapter));
        viewModel.getRefreshing().observe(this, swipeRefreshLayout::setRefreshing);
        swipeRefreshLayout.setOnRefreshListener(viewModel);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_library_list, menu);
        BaseActivity activity = (BaseActivity) getActivity();
        activity.tintToolbarIcons(menu, R.id.library_visit_catalogue, R.id.action_refresh);
        SearchView view = (SearchView) menu.findItem(R.id.action_search).getActionView();
        view.setOnQueryTextListener(adapter);
        view.setOnCloseListener(adapter);
        view.setOnSearchClickListener(v -> adapter.onOpen());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.library_visit_catalogue:
                NetworkUtils.maybeLaunchBrowser(getContext(), LIB_URL);
                return true;
            case R.id.action_refresh:
                viewModel.onRefresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(getView(), getString(R.string.failure), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.again), v -> viewModel.onRefresh())
                .show();
    }
}