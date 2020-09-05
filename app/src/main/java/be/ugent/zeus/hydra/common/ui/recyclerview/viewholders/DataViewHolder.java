package be.ugent.zeus.hydra.common.ui.recyclerview.viewholders;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.DataAdapter;

/**
 * A generic view holder, intended as root of all other view holders.
 * <p>
 * This view holder is designed to be used with an {@link DataAdapter}.
 *
 * @param <D> The type of the data this view holder displays.
 * @author Niko Strijbol
 */
public abstract class DataViewHolder<D> extends RecyclerView.ViewHolder {

    /**
     * The constructor is the place to bind views and other non-data related stuff.
     *
     * @param itemView The parent view.
     */
    public DataViewHolder(View itemView) {
        super(itemView);
    }

    /**
     * Receive the data that should be displayed in this view holder. A view holder may be used an unspecified amount
     * of times, meaning this method can be called multiple times for the same view holder. All data driven
     * initialisation should happen in this function.
     *
     * @param data The data
     */
    public abstract void populate(D data);
}