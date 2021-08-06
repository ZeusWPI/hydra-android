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

package be.ugent.zeus.hydra.common.ui.recyclerview.viewholders;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.DataAdapter;

/**
 * A generic view holder, intended as root of all other view holders.
 * <p>
 * This view holder is designed to be used with an {@link DataAdapter}.
 *
 * @param <D> The type of the data this view holder displays.
 * @author Niko Strijbol
 */
public abstract class DataViewHolder<D> extends RecyclerView.ViewHolder {

    /**
     * The constructor is the place to bind views and other non-data related stuff.
     *
     * @param itemView The parent view.
     */
    public DataViewHolder(View itemView) {
        super(itemView);
    }

    /**
     * Receive the data that should be displayed in this view holder. A view holder may be used an unspecified amount
     * of times, meaning this method can be called multiple times for the same view holder. All data driven
     * initialisation should happen in this function.
     *
     * @param data The data
     */
    public abstract void populate(D data);
}