package be.ugent.zeus.hydra.common.ui.recyclerview.adapters;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import java8.util.function.BiFunction;

import java.util.List;

/**
 * Adapter designed to work with a {@link DataViewHolder}.
 *
 * @author Niko Strijbol
 */
public abstract class ItemDiffAdapter<D, V extends DataViewHolder<D>> extends DiffAdapter<D, V> {

    /**
     * Initialize with a custom callback.
     *
     * @param callback The callback to use.
     */
    protected ItemDiffAdapter(DiffUtil.ItemCallback<D> callback) {
        super(callback);
    }

    protected ItemDiffAdapter() {
        super();
    }

    @Override
    public void onBindViewHolder(@NonNull V holder, int position) {
        holder.populate(items.get(position));
    }
}