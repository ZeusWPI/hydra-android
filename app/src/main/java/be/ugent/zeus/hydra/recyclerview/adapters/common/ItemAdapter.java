package be.ugent.zeus.hydra.recyclerview.adapters.common;

import be.ugent.zeus.hydra.recyclerview.viewholder.DataViewHolder;

/**
 * Adapter that works with a list of items.
 *
 * @param <E> The type of the elements the data of this adapter consists of.
 * @param <V> The type of the {@link android.support.v7.widget.RecyclerView.ViewHolder}.
 *
 * @author Niko Strijbol
 */
public abstract class ItemAdapter<E, V extends DataViewHolder<E>> extends SimpleItemAdapter<E, V> {

    @Override
    public void onBindViewHolder(V holder, int position) {
        holder.populate(items.get(position));
    }
}