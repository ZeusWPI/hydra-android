package be.ugent.zeus.hydra.fragments.library;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.HydraActivity;
import be.ugent.zeus.hydra.models.library.Library;
import be.ugent.zeus.hydra.loaders.plugin.LoaderProvider;
import be.ugent.zeus.hydra.plugins.RequestPlugin;
import be.ugent.zeus.hydra.plugins.common.Plugin;
import be.ugent.zeus.hydra.plugins.common.PluginFragment;
import be.ugent.zeus.hydra.recyclerview.TextCallback;
import be.ugent.zeus.hydra.recyclerview.adapters.LibraryListAdapter;
import be.ugent.zeus.hydra.recyclerview.adapters.common.EmptyItemAdapter;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;
import java8.util.function.Function;
import su.j2e.rvjoiner.JoinableAdapter;
import su.j2e.rvjoiner.JoinableLayout;
import su.j2e.rvjoiner.RvJoiner;

import java.util.List;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class LibraryListFragment extends PluginFragment {

    private static final String LIB_URL = "http://lib.ugent.be/";

    public static final String PREF_LIBRARY_FAVOURITES = "pref_library_favourites";

    private final RvJoiner joiner = new RvJoiner();
    private final LibraryListAdapter favourites = new LibraryListAdapter();
    private final LibraryListAdapter all = new LibraryListAdapter();
    private final RequestPlugin<Pair<List<Library>, List<Library>>> plugin =
            new RequestPlugin<>((Function<Boolean, LoaderProvider<Pair<List<Library>, List<Library>>>>) b -> (i, a) -> LibraryLoader.sortedLibrary(b, getContext()));

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library_list, container, false);
    }

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        plugin.defaultError()
                .hasProgress()
                .setDataCallback(this::receiveData);
        plugins.add(plugin);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = $(view, R.id.recycler_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        RecyclerFastScroller s = $(view, R.id.fast_scroller);
        s.attachRecyclerView(recyclerView);

        joiner.add(new JoinableLayout(R.layout.item_title, new TextCallback("Favorieten")));
        joiner.add(new JoinableAdapter(favourites, EmptyItemAdapter.ITEM_TYPE, EmptyItemAdapter.EMPTY_TYPE));
        joiner.add(new JoinableLayout(R.layout.item_title, new TextCallback("Alle")));
        joiner.add(new JoinableAdapter(all, EmptyItemAdapter.ITEM_TYPE, EmptyItemAdapter.EMPTY_TYPE));

        recyclerView.setAdapter(joiner.getAdapter());
    }

    private void receiveData(Pair<List<Library>, List<Library>> libraries) {
        favourites.setItems(libraries.second);
        all.setItems(libraries.first);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_library_list, menu);
        HydraActivity activity = (HydraActivity) getActivity();
        activity.tintToolbarIcons(menu, R.id.library_visit_catalogue);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.library_visit_catalogue:
                NetworkUtils.maybeLaunchBrowser(getContext(), LIB_URL);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}