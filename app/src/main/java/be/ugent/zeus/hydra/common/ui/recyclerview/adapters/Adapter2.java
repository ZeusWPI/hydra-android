package be.ugent.zeus.hydra.common.ui.recyclerview.adapters;

import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;

/**
 * Base type for adapters.
 *
 * @author Niko Strijbol
 */
public abstract class Adapter2<T, VH extends RecyclerView.ViewHolder> extends ListAdapter<T, VH> {

    /**
     * Create an adapter with a custom callback.
     *
     * @param diffCallback The callback.
     */
    protected Adapter2(@NonNull DiffUtil.ItemCallback<T> diffCallback) {
        super(diffCallback);
    }

    /**
     * Create an adapter using {@link EqualsItemCallback}.
     */
    protected Adapter2() {
        this(new EqualsItemCallback<>());
    }

    public void clear() {
        // Null is more efficient than an empty list.
        this.submitList(null);
    }
}