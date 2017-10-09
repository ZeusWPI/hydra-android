package be.ugent.zeus.hydra.ui.common.recyclerview.adapters;

import android.support.v7.util.DiffUtil;

import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;
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
    protected ItemDiffAdapter(BiFunction<List<D>, List<D>, DiffUtil.Callback> callback) {
        super(callback);
    }

    protected ItemDiffAdapter() {
        super();
    }

    @Override
    public void onBindViewHolder(V holder, int position) {
        holder.populate(items.get(position));
    }
}