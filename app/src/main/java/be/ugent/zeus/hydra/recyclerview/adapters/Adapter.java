package be.ugent.zeus.hydra.recyclerview.adapters;

import java.util.List;

import be.ugent.zeus.hydra.recyclerview.viewholder.AbstractViewHolder;

/**
 * @author Niko Strijbol
 * @version 28/07/2016
 */
public interface Adapter <E, V extends AbstractViewHolder<E>> {

    /**
     * Set the items. This use the provided items.
     *
     * This will also call {@link #notifyDataSetChanged()}.
     *
     * @param list The new elements.
     */
    void setItems(List<E> list);
}