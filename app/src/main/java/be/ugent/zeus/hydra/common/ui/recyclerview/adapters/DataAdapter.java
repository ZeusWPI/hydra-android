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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.RecycleViewHolder;

/**
 * Defines the basic adapter methods to work with {@link DataViewHolder}.
 *
 * @author Niko Strijbol
 */
public abstract class DataAdapter<D, VH extends DataViewHolder<D>> extends RecyclerView.Adapter<VH> {

    protected final DataContainer<D> dataContainer;

    DataAdapter() {
        this.dataContainer = new DataContainer<>(new AdapterListUpdateCallback(this));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.populate(item(position));
    }

    @Override
    public int getItemCount() {
        return dataContainer.data().size();
    }

    /**
     * Get the item that is currently at {@code position}.
     *
     * @param position The position of the item in the adapter's data set.
     * @return The item.
     */
    public D item(int position) {
        return dataContainer.data().get(position);
    }

    /**
     * Submit new data to the adapter.
     *
     * @param data The new data.
     */
    public abstract void submitData(List<D> data);

    /**
     * Remove all data from the adapter.
     */
    public abstract void clear();

    @Override
    public void onViewRecycled(@NonNull VH holder) {
        super.onViewRecycled(holder);
        // Notify ViewHolders that are interested in this event.
        if (holder instanceof RecycleViewHolder) {
            ((RecycleViewHolder) holder).onViewRecycled();
        }
    }
}