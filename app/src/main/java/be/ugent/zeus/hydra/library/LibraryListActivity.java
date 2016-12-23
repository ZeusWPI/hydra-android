package be.ugent.zeus.hydra.library;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.HydraActivity;
import be.ugent.zeus.hydra.loaders.DataCallback;
import be.ugent.zeus.hydra.plugins.common.Plugin;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class LibraryListActivity extends HydraActivity implements DataCallback<LibraryList> {

    private RequestPlugin<LibraryList> plugin;
    private LibraryListAdapter adapter;

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        plugin = new RequestPlugin<>(this, RequestPlugin.wrap(new LibraryListRequest()));
        plugins.add(plugin);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_list);

        adapter = new LibraryListAdapter();

        RecyclerView recyclerView = $(R.id.recycler_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        RecyclerFastScroller s = $(R.id.fast_scroller);
        s.attachRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);

        plugin.getLoaderPlugin().startLoader();
    }

    @Override
    public void receiveData(@NonNull LibraryList data) {
        adapter.setItems(data.getLibraries());
    }

    @Override
    public void receiveError(@NonNull Throwable e) {
        Log.w("Library", e);
    }
}