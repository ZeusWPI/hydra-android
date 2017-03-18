package be.ugent.zeus.hydra.ui.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.association.NewsItem;
import be.ugent.zeus.hydra.data.network.requests.NewsRequest;
import be.ugent.zeus.hydra.data.network.requests.Requests;
import be.ugent.zeus.hydra.ui.plugins.RecyclerViewPlugin;
import be.ugent.zeus.hydra.ui.plugins.common.Plugin;
import be.ugent.zeus.hydra.ui.plugins.common.PluginFragment;
import be.ugent.zeus.hydra.utils.recycler.SpanItemSpacingDecoration;

import java.util.List;

/**
 * Display DSA news.
 *
 * @author Ellen
 * @author Niko Strijbol
 */
public class NewsFragment extends PluginFragment {

    private final NewsAdapter adapter = new NewsAdapter();
    private final RecyclerViewPlugin<NewsItem> plugin = new RecyclerViewPlugin<>(Requests.cachedArray(new NewsRequest()), adapter);

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        plugin.defaultError().enableProgress();
        plugins.add(plugin);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        plugin.addItemDecoration(new SpanItemSpacingDecoration(getContext()));
    }
}