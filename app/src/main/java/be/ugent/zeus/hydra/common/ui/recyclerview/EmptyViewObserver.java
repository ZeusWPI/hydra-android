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

package be.ugent.zeus.hydra.common.ui.recyclerview;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Class that manages displaying and hiding the RecyclerView and the empty View.
 * <p>
 * If the adapter has no data, the View will displayed. If there is data, the RecyclerView will be displayed.
 *
 * @author Niko Strijbol
 */
public class EmptyViewObserver extends RecyclerView.AdapterDataObserver {

    private final RecyclerView recyclerView;
    private final View emptyView;
    private final RecyclerView.Adapter<?> adapter;

    /**
     * When using this constructor, you must register the adapter with the RecyclerView before calling this.
     *
     * @param recyclerView The RecyclerView. Will be shown when there is data.
     * @param emptyView    The View to display when there is no data.
     */
    public EmptyViewObserver(RecyclerView recyclerView, View emptyView) {
        this(recyclerView, emptyView, recyclerView.getAdapter());
    }

    /**
     * The RecyclerView must have access to the adapter before data events begin. This means you must have called
     * {@link RecyclerView#setAdapter(RecyclerView.Adapter)} before calling
     * {@link RecyclerView.Adapter#registerAdapterDataObserver(RecyclerView.AdapterDataObserver)}.
     *
     * @param recyclerView The RecyclerView. Will be shown when there is data.
     * @param emptyView    The View to display when there is no data.
     */
    public EmptyViewObserver(RecyclerView recyclerView, View emptyView, RecyclerView.Adapter<?> adapter) {
        this.recyclerView = recyclerView;
        this.emptyView = emptyView;
        this.adapter = adapter;
    }

    @Override
    public void onChanged() {
        if (adapter.getItemCount() == 0) {
            hide();
        } else {
            show();
        }
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount) {
        // This does not change the amount of items.
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
        // This does not change the amount of items.
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        if (itemCount > 0) {
            hide();
        }
    }

    @Override
    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        // This does not change the amount of items.
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
        if (adapter.getItemCount() - itemCount == 0) {
            show();
        }
    }

    private void show() {
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    private void hide() {
        recyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }
}
