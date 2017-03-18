package be.ugent.zeus.hydra.ui.common.recyclerview;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Extension of {@link ItemAdapter} that shows a specified view when there are no items.
 *
 * Note: due Java restrictions, this is not a child of {@link ItemAdapter}.
 *
 * @author Niko Strijbol
 */
public abstract class EmptyItemAdapter<E, V extends DataViewHolder<E>> extends Adapter<E, RecyclerView.ViewHolder> {

    public static final int EMPTY_TYPE = 1;

    @LayoutRes
    private int emptyViewId;

    public EmptyItemAdapter(@LayoutRes int emptyViewId) {
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
        return items.isEmpty() ? EMPTY_TYPE : super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == EMPTY_TYPE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(emptyViewId, parent, false);
            return new SimpleViewHolder(v);
        } else {
            return onCreateItemViewHolder(parent, viewType);
        }
    }

    protected abstract V onCreateItemViewHolder(ViewGroup parent, int viewType);

    /**
     * {@inheritDoc}
     *
     * This method checks the type of view holder. This is necessary, because the empty view holder cannot be bound.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // We use instance of here instead of getItemType(), because this makes it easy to work with RvJoiner.
        if (holder instanceof DataViewHolder) {
            @SuppressWarnings("unchecked")
            DataViewHolder<E> viewHolder = (DataViewHolder<E>) holder;
            viewHolder.populate(items.get(position));
        }
    }
}