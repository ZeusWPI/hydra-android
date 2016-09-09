package be.ugent.zeus.hydra.fragments.common;

import java.io.Serializable;
import java.util.List;

import be.ugent.zeus.hydra.recyclerview.adapters.common.ItemAdapter;

/**
 * @author Niko Strijbol
 */
public abstract class ItemAdapterLoaderFragment<E, D extends Serializable & List<E>> extends RecyclerLoaderFragment<E, D, ItemAdapter<E, ?>> {
}