package be.ugent.zeus.hydra.recyclerview.adapters;

import java.util.Collections;
import java.util.List;

import android.support.v7.widget.RecyclerView;

import be.ugent.zeus.hydra.recyclerview.viewholder.AbstractViewHolder;

/**
 * Adapter that works with a list of items.
 *
 * @param <E> The type of the elements the data of this adapter consists of.
 * @param <V> The type of the {@link android.support.v7.widget.RecyclerView.ViewHolder}.
 *
 * @author Niko Strijbol
 */
public abstract class ItemAdapter<E, V extends AbstractViewHolder<E>> extends RecyclerView.Adapter<V> implements Adapter<E, V> {

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

    @Override
    public void onBindViewHolder(V holder, int position) {
        holder.populate(items.get(position));
    }
}