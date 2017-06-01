package be.ugent.zeus.hydra.ui.main.library;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.library.Library;
import be.ugent.zeus.hydra.repository.RefreshBroadcast;
import be.ugent.zeus.hydra.repository.observers.ProgressObserver;
import be.ugent.zeus.hydra.repository.observers.SuccessObserver;
import be.ugent.zeus.hydra.repository.utils.ErrorUtils;
import be.ugent.zeus.hydra.ui.common.BaseActivity;
import be.ugent.zeus.hydra.ui.common.plugins.common.PluginFragment;
import be.ugent.zeus.hydra.ui.common.recyclerview.TextCallback;
import be.ugent.zeus.hydra.ui.common.recyclerview.adapters.EmptyItemAdapter;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;
import su.j2e.rvjoiner.JoinableAdapter;
import su.j2e.rvjoiner.JoinableLayout;
import su.j2e.rvjoiner.RvJoiner;

import java.util.List;

import static be.ugent.zeus.hydra.ui.common.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class LibraryListFragment extends PluginFragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "LibraryListFragment";
    private static final String LIB_URL = "http://lib.ugent.be/";
    public static final String PREF_LIBRARY_FAVOURITES = "pref_library_favourites";

    private final RvJoiner joiner = new RvJoiner();
    private final LibraryListAdapter favourites = new LibraryListAdapter(joiner);
    private final LibraryListAdapter all = new LibraryListAdapter(joiner);

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
        RecyclerView recyclerView = $(view, R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        RecyclerFastScroller s = $(view, R.id.fast_scroller);
        s.attachRecyclerView(recyclerView);

        joiner.add(new JoinableLayout(R.layout.item_title, new TextCallback("Favorieten")));
        joiner.add(new JoinableAdapter(favourites, EmptyItemAdapter.ITEM_TYPE, EmptyItemAdapter.EMPTY_TYPE));
        joiner.add(new JoinableLayout(R.layout.item_title, new TextCallback("Alle")));
        joiner.add(new JoinableAdapter(all, EmptyItemAdapter.ITEM_TYPE, EmptyItemAdapter.EMPTY_TYPE));

        recyclerView.setAdapter(joiner.getAdapter());

        SwipeRefreshLayout swipeRefreshLayout = $(view, R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.ugent_yellow_dark);
        swipeRefreshLayout.setOnRefreshListener(this);

        LibraryViewModel model = ViewModelProviders.of(this).get(LibraryViewModel.class);
        ErrorUtils.filterErrors(model.getData()).observe(this, this::onError);
        model.getData().observe(this, new ProgressObserver<>($(view, R.id.progress_bar)));
        model.getData().observe(this, new SuccessObserver<Pair<List<Library>, List<Library>>>() {
            @Override
            protected void onSuccess(Pair<List<Library>, List<Library>> data) {
                favourites.setItems(data.second);
                all.setItems(data.first);
            }
            @Override
            protected void onEmpty() {
                favourites.clear();
                all.clear();
            }
        });
        model.getRefreshing().observe(this, swipeRefreshLayout::setRefreshing);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_library_list, menu);
        BaseActivity activity = (BaseActivity) getActivity();
        activity.tintToolbarIcons(menu, R.id.library_visit_catalogue);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.library_visit_catalogue:
                NetworkUtils.maybeLaunchBrowser(getContext(), LIB_URL);
                return true;
            case R.id.action_refresh:
                onRefresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {
        RefreshBroadcast.broadcastRefresh(getContext(), true);
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(getView(), getString(R.string.failure), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.again), v -> onRefresh())
                .show();
    }
}