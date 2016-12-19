package be.ugent.zeus.hydra.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.association.News;
import be.ugent.zeus.hydra.models.association.NewsItem;
import be.ugent.zeus.hydra.plugins.RecyclerViewPlugin;
import be.ugent.zeus.hydra.plugins.RequestPlugin;
import be.ugent.zeus.hydra.plugins.common.Plugin;
import be.ugent.zeus.hydra.plugins.common.PluginFragment;
import be.ugent.zeus.hydra.recyclerview.adapters.NewsAdapter;
import be.ugent.zeus.hydra.requests.NewsRequest;
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
    private final RecyclerViewPlugin<NewsItem, News> plugin = new RecyclerViewPlugin<>(RequestPlugin.wrap(new NewsRequest()), adapter);

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
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