package be.ugent.zeus.hydra.library.list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.library.Library;
import be.ugent.zeus.hydra.plugins.RecyclerViewPlugin;
import be.ugent.zeus.hydra.plugins.common.Plugin;
import be.ugent.zeus.hydra.plugins.common.PluginFragment;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

import java.util.List;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class LibraryListFragment extends PluginFragment {

    private final LibraryListAdapter adapter = new LibraryListAdapter();
    private final RecyclerViewPlugin<Library, List<Library>> plugin = new RecyclerViewPlugin<>(SortedLibraryRequest::new, adapter);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library_list, container, false);
    }

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        plugin.defaultError()
                .hasProgress();
        plugins.add(plugin);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = plugin.getRecyclerView();
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        RecyclerFastScroller s = $(view, R.id.fast_scroller);
        s.attachRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);
    }
}