package be.ugent.zeus.hydra.fragments.sko;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.*;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.sko.Timeline;
import be.ugent.zeus.hydra.models.sko.TimelinePost;
import be.ugent.zeus.hydra.plugins.RecyclerViewPlugin;
import be.ugent.zeus.hydra.plugins.common.Plugin;
import be.ugent.zeus.hydra.plugins.common.PluginFragment;
import be.ugent.zeus.hydra.recyclerview.adapters.sko.TimelineAdapter;
import be.ugent.zeus.hydra.requests.sko.TimelineRequest;
import be.ugent.zeus.hydra.utils.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.utils.customtabs.CustomTabsHelper;
import be.ugent.zeus.hydra.utils.recycler.SpanItemSpacingDecoration;

import java.util.List;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Show a list of timeline posts for SKO.
 *
 * @author Niko Strijbol
 */
public class TimelineFragment extends PluginFragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout refreshLayout;
    private ActivityHelper helper;
    private final TimelineRequest request = new TimelineRequest();
    private RecyclerViewPlugin<TimelinePost, Timeline> plugin = RecyclerViewPlugin.cached(request, null);

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        plugin.hasProgress().defaultError().addResultListener(i -> refreshLayout.setRefreshing(false));
        plugins.add(plugin);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        helper = CustomTabsHelper.initHelper(getActivity(), null);
        helper.setShareMenu();
        plugin.setAdapter(new TimelineAdapter(helper));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sko_timeline, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshLayout = $(view, R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(this);
        plugin.addItemDecoration(new SpanItemSpacingDecoration(getContext()));
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
        plugin.refresh();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_refresh, menu);
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