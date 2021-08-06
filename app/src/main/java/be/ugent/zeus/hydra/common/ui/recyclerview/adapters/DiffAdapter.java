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

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;

/**
 * Generic adapter with support for calculating diffs on a background thread for data updates.
 *
 * <h1>View types</h1>
 * By default, the adapter only supports one view type. Additional types must be implemented by the
 * subclasses.
 *
 * <h1>Data updates</h1>
 * When calling {@link #submitData(List)}, a diff with the current data will be calculated on a background thread,
 * after which the changes will be dispatched to this adapter. This uses {@link androidx.recyclerview.widget.DiffUtil}
 * behind the scenes.
 * <p>
 * Subclasses can publish different types of updates directly to the {@link #dataContainer} by implementing
 * {@link AdapterUpdate}.
 *
 * @param <D>  The type of the items this adapter will contain. If multiple view types are needed, it is recommended
 *             to create a custom superclass of all the types.
 * @param <VH> The type of the view holder.
 * @author Niko Strijbol
 */
public abstract class DiffAdapter<D, VH extends DataViewHolder<D>> extends DataAdapter<D, VH> {

    protected final DiffUtil.ItemCallback<D> callback;

    public DiffAdapter() {
        this(new EqualsItemCallback<>());
    }

    public DiffAdapter(DiffUtil.ItemCallback<D> callback) {
        super();
        this.callback = callback;
    }

    @Override
    public void submitData(List<D> data) {
        dataContainer.submitUpdate(new DiffUpdate<>(callback, data));
    }

    @Override
    public void clear() {
        dataContainer.submitUpdate(new DiffUpdate<>(null));
    }
}