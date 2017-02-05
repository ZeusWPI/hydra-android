package be.ugent.zeus.hydra.recyclerview.adapters.common;

import be.ugent.zeus.hydra.recyclerview.viewholder.DataViewHolder;

/**
 * Adapter designed to work with a {@link DataViewHolder}.
 *
 * @author Niko Strijbol
 */
public abstract class ItemAdapter<D, V extends DataViewHolder<D>> extends Adapter<D, V> {

    @Override
    public void onBindViewHolder(V holder, int position) {
        holder.populate(items.get(position));
    }
}