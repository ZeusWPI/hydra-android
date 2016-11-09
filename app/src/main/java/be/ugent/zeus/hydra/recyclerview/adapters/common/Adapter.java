package be.ugent.zeus.hydra.recyclerview.adapters.common;

import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * @author Niko Strijbol
 * @version 28/07/2016
 */
public abstract class Adapter<E, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    /**
     * Set the items. This use the provided items.
     *
     * @param list The new elements.
     */
    public abstract void setItems(List<E> list);
}