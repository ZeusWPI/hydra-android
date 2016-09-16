package be.ugent.zeus.hydra.fragments.sko;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.cache.CacheRequest;
import be.ugent.zeus.hydra.fragments.common.CachedLoaderFragment;
import be.ugent.zeus.hydra.models.sko.Timeline;
import be.ugent.zeus.hydra.recyclerview.adapters.sko.TimelineAdapter;
import be.ugent.zeus.hydra.requests.sko.TimelineRequest;
import be.ugent.zeus.hydra.utils.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.utils.customtabs.CustomTabsHelper;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Show a list of timeline posts for SKO.
 *
 * @author Niko Strijbol
 */
public class TimelineFragment extends CachedLoaderFragment<Timeline> implements SwipeRefreshLayout.OnRefreshListener {

    private TimelineAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private ActivityHelper helper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        helper = CustomTabsHelper.initHelper(getActivity(), null);
        helper.setShareMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sko_timeline, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = $(view, R.id.recycler_view);

        refreshLayout = $(view, R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(this);

        adapter = new TimelineAdapter(helper);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onStart() {
        super.onStart();
        helper.bindCustomTabsService(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        helper.unbindCustomTabsService(getActivity());
    }

    @Override
    public void receiveData(@NonNull Timeline data) {
        adapter.setItems(data);
        refreshLayout.setRefreshing(false);
    }

    @Override
    public CacheRequest<Timeline, Timeline> getRequest() {
        return new TimelineRequest();
    }

    @Override
    public void onRefresh() {
        shouldRenew = true;
        restartLoader();
        shouldRenew = false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sko_refresh, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_refresh) {
            onRefresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}