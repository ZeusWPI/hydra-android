package be.ugent.zeus.hydra.ui.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.*;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.association.UgentNewsItem;
import be.ugent.zeus.hydra.data.network.requests.Requests;
import be.ugent.zeus.hydra.data.network.requests.association.UgentNewsRequest;
import be.ugent.zeus.hydra.ui.common.BaseActivity;
import be.ugent.zeus.hydra.ui.common.plugins.OfflinePlugin;
import be.ugent.zeus.hydra.ui.common.plugins.RecyclerViewPlugin;
import be.ugent.zeus.hydra.ui.common.plugins.common.Plugin;
import be.ugent.zeus.hydra.ui.common.plugins.common.PluginFragment;
import be.ugent.zeus.hydra.ui.common.recyclerview.SpanItemSpacingDecoration;

import java.util.List;

import static be.ugent.zeus.hydra.ui.common.ViewUtils.$;

/**
 * Display DSA news.
 *
 * @author Ellen
 * @author Niko Strijbol
 */
public class NewsFragment extends PluginFragment implements SwipeRefreshLayout.OnRefreshListener {

    private final NewsAdapter adapter = new NewsAdapter();
    private final RecyclerViewPlugin<UgentNewsItem> plugin = new RecyclerViewPlugin<>(Requests.cachedArray(new UgentNewsRequest()), adapter);
    private final OfflinePlugin offlinePlugin = new OfflinePlugin(this);

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        plugin.defaultError().enableProgress();
        plugin.setSuccessCallback(u -> swipeRefreshLayout.setRefreshing(false));
        plugins.add(plugin);
        plugins.add(offlinePlugin);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout = $(view, R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.ugent_yellow_dark);
        swipeRefreshLayout.setOnRefreshListener(this);
        plugin.addItemDecoration(new SpanItemSpacingDecoration(getContext()));
    }

    @Override
    public void onRefresh() {
        offlinePlugin.dismiss();
        swipeRefreshLayout.setRefreshing(true);
        plugin.refresh(false);
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
}