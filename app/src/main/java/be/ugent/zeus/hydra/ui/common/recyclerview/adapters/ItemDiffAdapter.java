package be.ugent.zeus.hydra.ui.common.recyclerview.adapters;

import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;

/**
 * Adapter designed to work with a {@link DataViewHolder}.
 *
 * @author Niko Strijbol
 */
public abstract class ItemDiffAdapter<D, V extends DataViewHolder<D>> extends DiffAdapter<D, V> {

    @Override
    public void onBindViewHolder(V holder, int position) {
        holder.populate(items.get(position));
    }
}