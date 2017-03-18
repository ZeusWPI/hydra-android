package be.ugent.zeus.hydra.ui.sko.overview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.view.*;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.sko.Exhibitor;
import be.ugent.zeus.hydra.data.network.requests.Requests;
import be.ugent.zeus.hydra.data.network.requests.sko.StuVilExhibitorRequest;
import be.ugent.zeus.hydra.ui.plugins.RecyclerViewPlugin;
import be.ugent.zeus.hydra.ui.plugins.common.Plugin;
import be.ugent.zeus.hydra.ui.plugins.common.PluginFragment;
import be.ugent.zeus.hydra.ui.common.BaseActivity;

import java.util.List;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class VillageFragment extends PluginFragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "VillageFragment";

    private SwipeRefreshLayout refreshLayout;
    private SearchView searchView;
    private ExhibitorAdapter adapter = new ExhibitorAdapter();
    private RecyclerViewPlugin<Exhibitor> plugin = new RecyclerViewPlugin<>(Requests.cachedArray(new StuVilExhibitorRequest()), adapter);

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        plugin.enableProgress().defaultError().addSuccessCallback(i -> refreshLayout.setRefreshing(false));
        plugins.add(plugin);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sko_village, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchView = $(view, R.id.search_view);
        searchView.setSuggestionsAdapter(null);
        searchView.setOnQueryTextListener(adapter);
        plugin.getRecyclerView().requestFocus();

        refreshLayout = $(view, R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(this);
        plugin.getRecyclerView().requestFocus();
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

    @Override
    public void onRefresh() {
        searchView.setQuery("", false);
        plugin.refresh();
    }
}