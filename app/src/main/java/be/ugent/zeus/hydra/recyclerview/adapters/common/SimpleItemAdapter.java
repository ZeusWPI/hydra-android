package be.ugent.zeus.hydra.recyclerview.adapters.common;

import android.support.v7.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

/**
 * Adapter that works with a list of items.
 *
 * @param <E> The type of the elements the data of this adapter consists of.
 * @param <V> The type of the {@link RecyclerView.ViewHolder}.
 *
 * @author Niko Strijbol
 */
public abstract class SimpleItemAdapter<E, V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> implements Adapter<E> {

    protected List<E> items = Collections.emptyList();

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Set the items. This use the provided items.
     *
     * This will also call {@link #notifyDataSetChanged()}.
     *
     * @param list The new elements.
     */
    public void setItems(List<E> list) {
        items = list;
        notifyDataSetChanged();
    }

    /**
     * Remove all items.
     */
    public void clear() {
        int size = items.size();
        items.clear();
        notifyItemRangeRemoved(0, size);
    }
}