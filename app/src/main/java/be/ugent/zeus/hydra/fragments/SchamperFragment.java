package be.ugent.zeus.hydra.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.schamper.Article;
import be.ugent.zeus.hydra.models.schamper.Articles;
import be.ugent.zeus.hydra.plugins.RecyclerViewPlugin;
import be.ugent.zeus.hydra.plugins.common.Plugin;
import be.ugent.zeus.hydra.plugins.common.PluginFragment;
import be.ugent.zeus.hydra.recyclerview.adapters.SchamperListAdapter;
import be.ugent.zeus.hydra.requests.SchamperArticlesRequest;
import be.ugent.zeus.hydra.utils.recycler.SpanItemSpacingDecoration;

import java.util.List;

/**
 * Display Schamper articles in the main activity.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class SchamperFragment extends PluginFragment {

    private final SchamperListAdapter adapter = new SchamperListAdapter();
    private final RecyclerViewPlugin<Article, Articles> plugin = new RecyclerViewPlugin<>(new SchamperArticlesRequest(), adapter);

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schamper, container, false);
    }

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        plugins.add(plugin);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = plugin.getRecyclerView();
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new SpanItemSpacingDecoration(getContext()));
    }
}