package be.ugent.zeus.hydra.common.ui.recyclerview.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.SearchView;

import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import java8.lang.Iterables;
import java8.util.function.BiPredicate;
import java8.util.function.Function;
import java8.util.function.Functions;
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
 * Currently searching is executed on the main thread. This imposes some limits, see the constructor description. This
 * is not guaranteed behaviour: searching may be executed in a different thread in the future.
 *
 * Users of this class should call the {@link #onOpen()} method. Not doing so may result in unreliable behaviour related
 * to the listeners.
 *
 * @param <D> The type used by the adapter and the type that should be displayed. See the description of {@link ItemDiffAdapter}.
 * @param <V> The view holder. See the description at {@link ItemDiffAdapter}.
 *
 * @author Niko Strijbol
 */
public abstract class GenericSearchableAdapter<D, V extends DataViewHolder<D>> extends ItemDiffAdapter<D, V> implements
        SearchView.OnQueryTextListener, SearchView.OnCloseListener, SearchHelper, android.widget.SearchView.OnQueryTextListener {

    protected List<D> allData = new ArrayList<>();
    private final BiPredicate<D, String> searchPredicate;
    private final Function<List<D>, List<D>> filter;

    private boolean isSearching;

    private final Set<SearchStateListener> listeners = Collections.newSetFromMap(new WeakHashMap<>());

    /**
     * @param searchPredicate The predicate used when searching. The predicate receives an item and the search query and
     *                        should return true if the item is a match for the given query. This should be fairly fast,
     *                        as it is executed for every item for every change in search query.
     */
    protected GenericSearchableAdapter(BiPredicate<D, String> searchPredicate) {
        super();
        this.searchPredicate = searchPredicate;
        this.filter = Functions.identity();
    }

    /**
     * @param searchPredicate The predicate used when searching. The predicate receives an item and the search query and
     *                        should return true if the item is a match for the given query. This should be fairly fast,
     *                        as it is executed for every item for every change in search query.
     */
    protected GenericSearchableAdapter(BiPredicate<D, String> searchPredicate, Function<List<D>, List<D>> filter) {
        super();
        this.searchPredicate = searchPredicate;
        this.filter = filter;
    }

    protected GenericSearchableAdapter(Function<D, String> stringifier) {
        this((d, s) -> stringifier.apply(d).contains(s));
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public void setItems(List<D> items) {
        this.allData = Collections.unmodifiableList(new ArrayList<>(items));
        super.setItems(items);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (!isSearching) {
            Iterables.forEach(listeners, listener -> listener.onSearchStateChange(true));
        }
        this.isSearching = true;
        List<D> filtered = StreamSupport.stream(allData)
                .filter(s -> searchPredicate.test(s, newText.toLowerCase()))
                .collect(Collectors.toList());
        filtered = filter.apply(filtered);
        super.setItems(filtered);
        return true;
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