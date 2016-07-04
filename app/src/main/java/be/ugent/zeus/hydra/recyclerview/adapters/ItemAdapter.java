package be.ugent.zeus.hydra.recyclerview.adapters;

import android.support.v7.widget.RecyclerView;
import be.ugent.zeus.hydra.recyclerview.viewholder.DataViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Adapter that works with a list of items.
 *
 * @param <E> The type of the elements the data of this adapter consists of.
 * @param <V> The type of the {@link android.support.v7.widget.RecyclerView.ViewHolder}.
 *
 * @author Niko Strijbol
 */
public abstract class ItemAdapter<E, V extends RecyclerView.ViewHolder & DataViewHolder<E>> extends RecyclerView.Adapter<V> {

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
     * Set the items. This use the provided items. If you use collections and want a new collection, you should do
     *
     *
     * This will also call {@link #notifyDataSetChanged()}.
     *
     * @param list The new elements.
     */
    public void setItems(List<E> list) {
        items = new ArrayList<>();
        items.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(V holder, int position) {
        holder.populateData(items.get(position));
    }
}