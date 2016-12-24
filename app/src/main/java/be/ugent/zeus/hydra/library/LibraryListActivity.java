package be.ugent.zeus.hydra.library;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.HydraActivity;
import be.ugent.zeus.hydra.plugins.RequestPlugin;
import be.ugent.zeus.hydra.plugins.common.Plugin;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class LibraryListActivity extends HydraActivity {

    private final RequestPlugin<LibraryList> plugin = RequestPlugin.cached(new LibraryListRequest());
    private final LibraryListAdapter adapter = new LibraryListAdapter();

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        plugin.defaultError()
                .hasProgress()
                .setDataCallback(d -> adapter.setItems(d.getLibraries()));
        plugins.add(plugin);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_list);

        RecyclerView recyclerView = $(R.id.recycler_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        RecyclerFastScroller s = $(R.id.fast_scroller);
        s.attachRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);

        plugin.startLoader();
    }
}