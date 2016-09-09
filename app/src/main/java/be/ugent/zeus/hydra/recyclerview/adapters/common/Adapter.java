package be.ugent.zeus.hydra.recyclerview.adapters.common;

import java.util.List;

/**
 * @author Niko Strijbol
 * @version 28/07/2016
 */
public interface Adapter <E> {

    /**
     * Set the items. This use the provided items.
     *
     * @param list The new elements.
     */
    void setItems(List<E> list);
}