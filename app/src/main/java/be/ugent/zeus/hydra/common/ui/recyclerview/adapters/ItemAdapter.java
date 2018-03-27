package be.ugent.zeus.hydra.common.ui.recyclerview.adapters;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;

/**
 * Adapter designed to work with a {@link DataViewHolder}.
 *
 * @author Niko Strijbol
 */
public abstract class ItemAdapter<D, V extends DataViewHolder<D>> extends Adapter<D, V> {

    @Override
    public void onBindViewHolder(@NonNull V holder, int position) {
        holder.populate(items.get(position));
    }
}