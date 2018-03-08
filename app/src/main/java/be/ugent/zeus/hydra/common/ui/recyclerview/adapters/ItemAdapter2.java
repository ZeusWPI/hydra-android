package be.ugent.zeus.hydra.common.ui.recyclerview.adapters;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;

/**
 * @author Niko Strijbol
 */
public abstract class ItemAdapter2<T, VH extends DataViewHolder<T>> extends Adapter2<T, VH> {

    /**
     * Create an adapter with a custom callback.
     *
     * @param diffCallback The callback.
     */
    protected ItemAdapter2(@NonNull DiffUtil.ItemCallback<T> diffCallback) {
        super(diffCallback);
    }

    /**
     * Create an adapter using {@link EqualsItemCallback}.
     */
    protected ItemAdapter2() {
        super();
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.populate(getItem(position));
    }
}