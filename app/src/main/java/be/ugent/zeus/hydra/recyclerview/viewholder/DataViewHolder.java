package be.ugent.zeus.hydra.recyclerview.viewholder;

import android.support.v7.widget.RecyclerView;

/**
 * @author Niko Strijbol
 * @version 20/06/2016
 */
public interface DataViewHolder<D> {

    /**
     * Populate with the data. This method must be called in {@link android.support.v7.widget.RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int)}
     * @param data The data.
     */
    void populateData(final D data);
}