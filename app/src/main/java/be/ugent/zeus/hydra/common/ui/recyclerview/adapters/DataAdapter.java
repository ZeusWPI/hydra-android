package be.ugent.zeus.hydra.common.ui.recyclerview.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;

import java.util.List;

/**
 * Defines the basic adapter methods to work with {@link DataViewHolder}.
 *
 * @author Niko Strijbol
 */
public abstract class DataAdapter<D, VH extends DataViewHolder<D>> extends RecyclerView.Adapter<VH> {

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.populate(getItem(position));
    }

    public abstract D getItem(int position);

    /**
     * Submit new data to the adapter.
     *
     * @param data The new data.
     */
    public abstract void submitData(List<D> data);

    public abstract void clear();
}