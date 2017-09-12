package be.ugent.zeus.hydra.ui.common.recyclerview.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.SearchView;

import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.ui.main.minerva.SearchHelper;
import be.ugent.zeus.hydra.ui.main.minerva.SearchStateListener;
import java8.lang.Iterables;
import java8.util.function.BiPredicate;
import java8.util.function.Function;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

import java.util.*;

/**
 * Adapter that provides an implementation of the necessary interfaces to integrate it with {@link SearchView}. The
 * implementation might look like this:
 * <pre>
 * {@code
 * searchView.setOnQueryTextListener(adapter);
 * searchView.setOnCloseListener(adapter);
 * searchView.setOnSearchClickListener(v -> adapter.onOpen());
 * }
 * </pre>
 *
 * This is a very generic class, supporting a lot of situations, including adapters with multiple
 * view types.
 *
 * A simpler and probably the version you are looking for is {@link SearchableDiffAdapter}.
 *
 * Currently searching is executed on the main thread. This imposes some limits, see the constructor description. This
 * is not guaranteed behaviour: searching may be executed in a different thread in the future.
 *
 * Users of this class should call the {@link #onOpen()} method. Not doing so may result in unreliable behaviour related
 * to the listeners.
 *
 * @param <D> The type used by the adapter and the type that should be displayed. See the description of {@link ItemDiffAdapter}.
 * @param <V> The view holder. See the description at {@link ItemDiffAdapter}.
 * @param <S> The type of the data in which the search is performed.
 *
 * @author Niko Strijbol
 */
public abstract class GenericSearchableAdapter<D, V extends DataViewHolder<D>, S> extends ItemDiffAdapter<D, V> implements
        SearchView.OnQueryTextListener, SearchView.OnCloseListener, SearchHelper, android.widget.SearchView.OnQueryTextListener {

    protected List<S> allData = new ArrayList<>();
    private final BiPredicate<S, String> searchPredicate;
    private final Function<List<D>, List<S>> converter;
    private final Function<List<S>, List<D>> reverser;

    private boolean isSearching;

    private final Set<SearchStateListener> listeners = Collections.newSetFromMap(new WeakHashMap<>());

    /**
     * @param searchPredicate The predicate used when searching. The predicate receives an item and the search query and
     *                        should return true if the item is a match for the given query. This should be fairly fast,
     *                        as it is executed for every item for every change in search query.
     * @param converter Convert the list of data to the list of searchable data. This function is called every time
     *                  {@link #setItems(List)} is called.
     * @param reverser Convert a list of searchable items back to a list of presentable items. This method should be
     *                 fairly fast, as it is called every time the search query changes.
     */
    protected GenericSearchableAdapter(BiPredicate<S, String> searchPredicate, Function<List<D>, List<S>> converter, Function<List<S>, List<D>> reverser) {
        super();
        this.converter = converter;
        this.searchPredicate = searchPredicate;
        this.reverser = reverser;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public void setItems(List<D> items) {
        this.allData = converter.apply(items);
        super.setItems(items);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (!isSearching) {
            Iterables.forEach(listeners, listener -> listener.onSearchStateChange(true));
        }
        this.isSearching = true;
        List<S> filtered = StreamSupport.stream(allData)
                .filter(s -> searchPredicate.test(s, newText.toLowerCase()))
                .collect(Collectors.toList());
        setUpdate(reverser.apply(filtered));
        return true;
    }

    /**
     * Same as {@link #setItems(List)}, but does not update the original data. This means the adapter's current 'live'
     * data will be changed, but when search is finished, the original data will be restored. On the contrary, {@link #setItems(List)}
     * will also set the original data.
     *
     * @param items The items.
     */
    protected void setUpdate(List<D> items) {
        synchronized (updateLock) {
            if (isDiffing) {
                scheduledUpdate = items;
            } else {
                updateItemInternal(items);
                isDiffing = true;
            }
        }
    }

    @Override
    public boolean onClose() {
        if (isSearching) {
            Iterables.forEach(listeners, listener -> listener.onSearchStateChange(false));
        }
        this.isSearching = false;
        return false;
    }

    /**
     * Should be called when the search view opens.
     */
    public void onOpen() {
        if (!isSearching) {
            Iterables.forEach(listeners, listener -> listener.onSearchStateChange(true));
        }
        this.isSearching = true;
        Iterables.forEach(listeners, listener -> listener.onSearchStateChange(isSearching));
    }

    @Override
    public boolean isSearching() {
        return isSearching;
    }

    @Override
    public void registerSearchListener(@NonNull SearchStateListener listener) {
        listeners.add(listener);
    }

    @Override
    public void unregisterSearchListener(@NonNull SearchStateListener listener) {
        listeners.remove(listener);
    }
}