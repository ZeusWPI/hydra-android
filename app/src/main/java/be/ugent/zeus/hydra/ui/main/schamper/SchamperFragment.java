package be.ugent.zeus.hydra.ui.main.schamper;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.schamper.Article;
import be.ugent.zeus.hydra.repository.RefreshBroadcast;
import be.ugent.zeus.hydra.repository.extensions.RecyclerHelper;
import be.ugent.zeus.hydra.ui.common.BaseActivity;
import be.ugent.zeus.hydra.ui.common.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.ui.common.customtabs.CustomTabsHelper;
import be.ugent.zeus.hydra.ui.common.plugins.ProgressBarPlugin;
import be.ugent.zeus.hydra.ui.common.plugins.common.Plugin;
import be.ugent.zeus.hydra.ui.common.plugins.common.PluginFragment;
import be.ugent.zeus.hydra.ui.common.recyclerview.SpanItemSpacingDecoration;

import java.util.List;

/**
 * Display Schamper articles in the main activity.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class SchamperFragment extends PluginFragment {

    private final RecyclerHelper<Article> plugin = new RecyclerHelper<>();
    private final ProgressBarPlugin progressBarPlugin = new ProgressBarPlugin();
    private ActivityHelper helper;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schamper, container, false);
    }

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        plugins.add(plugin);
        plugins.add(progressBarPlugin);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = CustomTabsHelper.initHelper(getActivity(), null);
        helper.setShareMenu();
        plugin.setAdapter(new SchamperListAdapter(helper));
        SchamperViewModel viewModel = ViewModelProviders.of(this).get(SchamperViewModel.class);

        viewModel.getData().observe(this, progressBarPlugin::onChanged);
        viewModel.getData().observe(this, plugin);
        setHasOptionsMenu(true);
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = plugin.getRecyclerView();
        recyclerView.setHasFixedSize(true);
        plugin.addItemDecoration(new SpanItemSpacingDecoration(getContext()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_refresh) {
            onRefresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onRefresh() {
        RefreshBroadcast.broadcastRefresh(getContext(), true);
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
}