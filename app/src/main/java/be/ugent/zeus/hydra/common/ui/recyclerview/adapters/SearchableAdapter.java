/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra.common.ui.recyclerview.adapters;

import androidx.appcompat.widget.SearchView;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;

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
 * <p>
 * This is a very generic class, supporting a lot of situations, including adapters with multiple
 * view types.
 * <p>
 * Currently searching is executed on the main thread. This imposes some limits, see the constructor description. This
 * is not guaranteed behaviour: searching may be executed in a different thread in the future.
 * <p>
 * Users of this class should call the {@link #onOpen()} method. Not doing so may result in unreliable behaviour related
 * to the listeners.
 *
 * @author Niko Strijbol
 */
public abstract class SearchableAdapter<D, VH extends DataViewHolder<D>> extends DiffAdapter<D, VH> implements
        SearchView.OnQueryTextListener, SearchView.OnCloseListener, android.widget.SearchView.OnQueryTextListener, android.widget.SearchView.OnCloseListener {

    private final BiPredicate<D, String> searchPredicate;
    private final Function<List<D>, List<D>> filter;
    private final Set<SearchStateListener> listeners = Collections.newSetFromMap(new WeakHashMap<>());
    private List<D> allData = Collections.emptyList();
    private boolean isSearching;

    /**
     * @param searchPredicate The predicate used when searching. The predicate receives an item and the search query and
     *                        should return true if the item is a match for the given query. This should be fairly fast,
     *                        as it is executed for every item for every change in search query.
     */
    protected SearchableAdapter(BiPredicate<D, String> searchPredicate) {
        super();
        this.searchPredicate = searchPredicate;
        this.filter = Function.identity();
    }

    protected SearchableAdapter(Function<D, String> stringifier) {
        this((d, s) -> stringifier.apply(d).contains(s));
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public void submitData(List<D> data) {
        this.allData = List.copyOf(data);
        super.submitData(data);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (!isSearching) {
            for (SearchStateListener listener : listeners) {
                listener.onSearchStateChange(true);
            }
        }
        this.isSearching = true;
        List<D> filtered = allData.stream()
                .filter(s -> searchPredicate.test(s, newText.toLowerCase(Locale.getDefault())))
                .collect(Collectors.toList());
        filtered = filter.apply(filtered);
        super.submitData(filtered);
        return true;
    }

    @Override
    public boolean onClose() {
        if (isSearching) {
            for (SearchStateListener listener : listeners) {
                listener.onSearchStateChange(false);
            }
        }
        this.isSearching = false;
        return false;
    }

    /**
     * Should be called when the search view opens.
     */
    public void onOpen() {
        if (!isSearching) {
            for (SearchStateListener listener : listeners) {
                listener.onSearchStateChange(true);
            }
        }
        this.isSearching = true;
        for (SearchStateListener listener : listeners) {
            listener.onSearchStateChange(isSearching);
        }
    }
}
