package be.ugent.zeus.hydra.recyclerview.adapters.common;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.recyclerview.viewholder.DataViewHolder;
import be.ugent.zeus.hydra.recyclerview.viewholder.SimpleViewHolder;

/**
 * Extension of {@link ItemAdapter} that shows a specified view when there are no items.
 *
 * @author Niko Strijbol
 */
public abstract class EmptyItemLoader<E, V extends DataViewHolder<E>> extends SimpleItemAdapter<E, RecyclerView.ViewHolder> {

    public static final int ITEMS_VIEW = 0;
    public static final int EMPTY_VIEW = 1;

    @LayoutRes
    protected int emptyViewId;

    public EmptyItemLoader(@LayoutRes int emptyViewId) {
        this.emptyViewId = emptyViewId;
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return items.isEmpty() ? 1 : super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return items.isEmpty() ? EMPTY_VIEW : super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType == EMPTY_VIEW) {
            View v =LayoutInflater.from(parent.getContext()).inflate(this.emptyViewId, parent, false);
            return new SimpleViewHolder(v);
        } else {
            return onCreateItemViewHolder(parent, viewType);
        }
    }

    protected abstract V onCreateItemViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof DataViewHolder) {
            @SuppressWarnings("unchecked")
            DataViewHolder<E> viewHolder = (DataViewHolder<E>) holder;
            viewHolder.populate(items.get(position));
        }
    }
}