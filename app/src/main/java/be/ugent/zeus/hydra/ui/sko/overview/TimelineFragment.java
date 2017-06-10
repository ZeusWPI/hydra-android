package be.ugent.zeus.hydra.ui.sko.overview;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.repository.RefreshBroadcast;
import be.ugent.zeus.hydra.repository.observers.AdapterObserver;
import be.ugent.zeus.hydra.repository.observers.ErrorObserver;
import be.ugent.zeus.hydra.repository.observers.ProgressObserver;
import be.ugent.zeus.hydra.ui.common.BaseActivity;
import be.ugent.zeus.hydra.ui.common.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.ui.common.customtabs.CustomTabsHelper;
import be.ugent.zeus.hydra.ui.common.recyclerview.SpanItemSpacingDecoration;

import static be.ugent.zeus.hydra.ui.common.ViewUtils.$;

/**
 * Show a list of timeline posts for SKO.
 *
 * @author Niko Strijbol
 */
public class TimelineFragment extends LifecycleFragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "TimelineFragment";

    private ActivityHelper helper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        helper = CustomTabsHelper.initHelper(getActivity(), null);
        helper.setShareMenu();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sko_timeline, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SwipeRefreshLayout refreshLayout = $(view, R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.sko_red);
        refreshLayout.setOnRefreshListener(this);

        TimelineAdapter adapter = new TimelineAdapter(helper);

        RecyclerView recyclerView = $(view, R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new SpanItemSpacingDecoration(getContext()));
        recyclerView.setAdapter(adapter);

        TimelineViewModel model = ViewModelProviders.of(this).get(TimelineViewModel.class);
        model.getData().observe(this, ErrorObserver.with(this::onError));
        model.getData().observe(this, new ProgressObserver<>($(view, R.id.progress_bar)));
        model.getData().observe(this, new AdapterObserver<>(adapter));
        model.getRefreshing().observe(this, refreshLayout::setRefreshing);
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
    public void onRefresh() {
        RefreshBroadcast.broadcastRefresh(getContext(), true);
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(getView(), getString(R.string.failure), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.again), v -> onRefresh())
                .show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_refresh, menu);
        BaseActivity activity = (BaseActivity) getActivity();
        BaseActivity.tintToolbarIcons(activity.getToolbar(), menu, R.id.action_refresh);
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