package be.ugent.zeus.hydra.ui.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.info.InfoItem;
import be.ugent.zeus.hydra.data.network.requests.InfoRequest;
import be.ugent.zeus.hydra.data.network.requests.Requests;
import be.ugent.zeus.hydra.ui.InfoSubItemActivity;
import be.ugent.zeus.hydra.ui.plugins.ProgressBarPlugin;
import be.ugent.zeus.hydra.ui.plugins.RecyclerViewPlugin;
import be.ugent.zeus.hydra.ui.plugins.common.Plugin;
import be.ugent.zeus.hydra.ui.plugins.common.PluginFragment;

import java.util.List;

/**
 * Display info items.
 *
 * @author Niko Strijbol
 */
public class InfoFragment extends PluginFragment {

    private final InfoListAdapter adapter = new InfoListAdapter();
    private final RecyclerViewPlugin<InfoItem> recyclerPlugin = new RecyclerViewPlugin<>(Requests.cachedArray(new InfoRequest()), adapter);

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        recyclerPlugin.defaultError()
                .enableProgress()
                .setAutoStart(false);
        plugins.add(recyclerPlugin);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.getParcelableArrayList(InfoSubItemActivity.INFO_ITEMS) != null) {
            recyclerPlugin.receiveData(bundle.getParcelableArrayList(InfoSubItemActivity.INFO_ITEMS));
            recyclerPlugin.getProgressBarPlugin().ifPresent(ProgressBarPlugin::hideProgressBar);
        } else {
            recyclerPlugin.startLoader();
        }
    }
}