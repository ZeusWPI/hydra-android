package be.ugent.zeus.hydra.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.InfoSubItemActivity;
import be.ugent.zeus.hydra.models.info.InfoItem;
import be.ugent.zeus.hydra.models.info.InfoList;
import be.ugent.zeus.hydra.plugins.ProgressBarPlugin;
import be.ugent.zeus.hydra.plugins.RecyclerViewPlugin;
import be.ugent.zeus.hydra.plugins.common.Plugin;
import be.ugent.zeus.hydra.plugins.common.PluginFragment;
import be.ugent.zeus.hydra.recyclerview.adapters.InfoListAdapter;
import be.ugent.zeus.hydra.requests.InfoRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Display info items.
 *
 * @author Niko Strijbol
 */
public class InfoFragment extends PluginFragment {

    private final InfoListAdapter adapter = new InfoListAdapter();
    private final RecyclerViewPlugin<InfoItem, InfoList> recyclerPlugin = RecyclerViewPlugin.cached(new InfoRequest(), adapter);

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        recyclerPlugin.defaultError()
                .hasProgress()
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
            InfoList infoItems = new InfoList();
            ArrayList<InfoItem> list = bundle.getParcelableArrayList(InfoSubItemActivity.INFO_ITEMS);
            assert list != null;
            infoItems.addAll(list);
            recyclerPlugin.receiveData(infoItems);
            recyclerPlugin.getProgressBarPlugin().ifPresent(ProgressBarPlugin::hideProgressBar);
        } else {
            recyclerPlugin.startLoader();
        }
    }
}