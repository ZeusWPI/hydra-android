package be.ugent.zeus.hydra.common.ui.recyclerview.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Fairly simple and generic adapter, holding items in a list.
 *
 * Generally, you should use one of it's two children: {@link DiffAdapter} or {@link MultiSelectDiffAdapter}.
 *
 * @param <D> The type of items used in the adapter.
 * @param <VH> The type of the view holder used by the adapter.
 *
 * @author Niko Strijbol
 */
public abstract class Adapter<D, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    public static final int ITEM_TYPE = 0;

    protected List<D> items = Collections.emptyList();

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Set the items in the adapter. The adapter will use the provided list. Modifying the list after having called
     * this function is undefined behaviour; it might crash everything. You should make a copy of the data if you
     * need to modify it. Note that the adapter has full control over the list; it might modify it.
     *
     * This will also call {@link #notifyDataSetChanged()}.
     *
     * Implementation note: the reason why the adapter does not copy the items is for performance: there might be a lot
     * of items and this function generally runs on the main thread.
     *
     * @param items The new elements.
     */
    public void setItems(List<D> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return ITEM_TYPE;
    }

    /**
     * Remove all items from the adapter.
     */
    public void clear() {
        int size = items.size();
        items = Collections.emptyList();
        notifyItemRangeRemoved(0, size);
    }

    /**
     * Get the items in the adapter. The returned collection cannot be modified. This is enforced by the adapter: the
     * returned collection is read-only.
     *
     * @return An unmodifiable list of the items.
     */
    @NonNull
    public Collection<D> getItems() {
        return Collections.unmodifiableCollection(items);
    }
}