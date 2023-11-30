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

import android.util.Pair;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;

/**
 * Basic searchable adapter with basic multi-select support. Extending a {@link SearchableAdapter} to be multi-select
 * or a {@link MultiSelectAdapter} to be searchable is very difficult and complicated, while we don't need all that
 * stuff.
 *
 * @author Niko Strijbol
 */
public abstract class MultiSelectSearchableAdapter<D, VH extends DataViewHolder<Pair<D, Boolean>>> extends DataAdapter<Pair<D, Boolean>, VH> implements
        SearchView.OnQueryTextListener, android.widget.SearchView.OnQueryTextListener {

    private final Function<String, Predicate<D>> searchPredicate;
    protected List<Pair<D, Boolean>> allData = Collections.emptyList();

    protected MultiSelectSearchableAdapter(Function<String, Predicate<D>> searchPredicate) {
        this.searchPredicate = searchPredicate;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public void submitData(List<Pair<D, Boolean>> data) {
        allData = new ArrayList<>(data);
        setData(data);
    }

    private void setData(List<Pair<D, Boolean>> data) {
        dataContainer.submitUpdate(new DumbUpdate<>(data));
    }

    @Override
    public void clear() {
        dataContainer.submitUpdate(new DumbUpdate<>(Collections.emptyList()));
    }

    /**
     * Change the boolean value at the given position to the reverse value. This operation DOES NOT trigger a change in
     * the recycler view data, so the methods for changed items is not called. The rationale behind this is that this
     * method is meant to be attached to a checkbox as a listener. Then the checkbox is already updated.
     *
     * @param position The position.
     */
    public void setChecked(int position) {
        dataContainer.submitUpdate(new AdapterUpdate<>() {
            @Override
            public List<Pair<D, Boolean>> newData(List<Pair<D, Boolean>> existingData) {

                Pair<D, Boolean> data = item(position);
                Pair<D, Boolean> newData = new Pair<>(data.first, !data.second);

                existingData.remove(data);
                existingData.add(position, newData);

                int allDataIndex = allData.indexOf(data);
                allData.remove(data);
                allData.add(allDataIndex, newData);

                return existingData;
            }

            @Override
            public void applyUpdatesTo(ListUpdateCallback listUpdateCallback) {
                notifyItemChanged(position);
            }

            @Override
            public boolean shouldUseMultiThreading() {
                return false;
            }
        });
    }

    /**
     * Set all items to the given boolean. This operation DOES trigger a layout update.
     *
     * @param checked The value.
     */
    public void setAllChecked(boolean checked) {
        dataContainer.submitUpdate(new AdapterUpdate<>() {
            @Override
            @Nullable
            public List<Pair<D, Boolean>> newData(@Nullable List<Pair<D, Boolean>> existingData) {

                if (existingData == null) {
                    return null;
                }

                allData = allData.stream()
                        .map(p -> new Pair<>(p.first, checked))
                        .collect(Collectors.toList());

                return existingData.stream()
                        .map(p -> new Pair<>(p.first, checked))
                        .collect(Collectors.toList());
            }

            @Override
            public void applyUpdatesTo(ListUpdateCallback listUpdateCallback) {
                notifyItemRangeChanged(0, getItemCount());
            }

            @Override
            public boolean shouldUseMultiThreading() {
                return false;
            }
        });
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Predicate<D> predicate = searchPredicate.apply(newText);
        List<Pair<D, Boolean>> newData = allData.stream()
                .filter(dBooleanPair -> predicate.test(dBooleanPair.first))
                .collect(Collectors.toList());
        dataContainer.submitUpdate(new DumbUpdate<>(newData));
        return true;
    }
}