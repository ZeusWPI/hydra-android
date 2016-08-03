package be.ugent.zeus.hydra.recyclerview.adapters;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.recyclerview.viewholder.AbstractViewHolder;

/**
 * @author Niko Strijbol
 * @version 28/07/2016
 */
public abstract class DataAdapter<E, V extends AbstractViewHolder<E>> extends ItemAdapter<E, V> {

    @LayoutRes
    private final int layoutResource;

    public DataAdapter(@LayoutRes int layoutResource) {
        this.layoutResource = layoutResource;
    }

    @Override
    public V onCreateViewHolder(ViewGroup parent, int viewType) {
        return createViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutResource, parent, false));
    }

    protected abstract V createViewHolder(@NonNull View view);
}
