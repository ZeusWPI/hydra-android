package be.ugent.zeus.hydra.common.ui.recyclerview.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.RecycleViewHolder;

/**
 * Defines the basic adapter methods to work with {@link DataViewHolder}.
 *
 * @author Niko Strijbol
 */
public abstract class DataAdapter<D, VH extends DataViewHolder<D>> extends RecyclerView.Adapter<VH> {

    protected final DataContainer<D> dataContainer;

    DataAdapter() {
        this.dataContainer = new DataContainer<>(new AdapterListUpdateCallback(this));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.populate(getItem(position));
    }

    @Override
    public int getItemCount() {
        return dataContainer.getData().size();
    }

    /**
     * Get the item that is currently at {@code position}.
     *
     * @param position The position of the item in the adapter's data set.
     * @return The item.
     */
    public D getItem(int position) {
        return dataContainer.getData().get(position);
    }

    /**
     * Submit new data to the adapter.
     *
     * @param data The new data.
     */
    public abstract void submitData(List<D> data);

    /**
     * Remove all data from the adapter.
     */
    public abstract void clear();

    @Override
    public void onViewRecycled(@NonNull VH holder) {
        super.onViewRecycled(holder);
        // Notify ViewHolders that are interested in this event.
        if (holder instanceof RecycleViewHolder) {
            ((RecycleViewHolder) holder).onViewRecycled();
        }
    }
}