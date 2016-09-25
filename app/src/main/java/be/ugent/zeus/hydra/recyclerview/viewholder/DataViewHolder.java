package be.ugent.zeus.hydra.recyclerview.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * A view holder that displays data.
 *
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