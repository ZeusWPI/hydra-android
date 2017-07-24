package be.ugent.zeus.hydra.ui.common.recyclerview.adapters;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.SimpleViewHolder;
import su.j2e.rvjoiner.RvJoiner;

/**
 * Extension of {@link ItemAdapter} that shows a specified view when there are no items.
 *
 * Note: due Java restrictions, this is not a child of {@link ItemAdapter}.
 *
 * @author Niko Strijbol
 */
public abstract class EmptyItemSelectAdapter<E> extends MultiSelectListAdapter<E> {

    public static final int EMPTY_TYPE = 1;

    private RvJoiner rvJoiner;

    @LayoutRes
    private int emptyViewId;

    public EmptyItemSelectAdapter(@LayoutRes int emptyViewId, @Nullable RvJoiner rvJoiner) {
        this.emptyViewId = emptyViewId;
        this.rvJoiner = rvJoiner;
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
    public DataViewHolder<Pair<E, Boolean>> onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == EMPTY_TYPE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(emptyViewId, parent, false);
            return new SimpleViewHolder<>(v);
        } else {
            return onCreateItemViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(DataViewHolder<Pair<E, Boolean>> holder, int position) {
        int type;
        if (rvJoiner != null) {
            // If we are using RvJoiner, we must get the real type, not the joined one.
            // See the repository for more information.
            type = rvJoiner.getPositionInfo(holder.getAdapterPosition()).realType;
        } else {
            type = holder.getItemViewType();
        }

        if (type != EMPTY_TYPE) {
            super.onBindViewHolder(holder, position);
        }
    }

    protected abstract DataViewHolder<Pair<E, Boolean>> onCreateItemViewHolder(ViewGroup parent, int viewType);
}