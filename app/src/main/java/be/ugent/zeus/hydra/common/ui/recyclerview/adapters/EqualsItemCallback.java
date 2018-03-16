package be.ugent.zeus.hydra.common.ui.recyclerview.adapters;

import android.support.v7.util.DiffUtil;

/**
 * @author Niko Strijbol
 */
public class EqualsItemCallback<T> extends DiffUtil.ItemCallback<T> {

    @Override
    public boolean areItemsTheSame(T oldItem, T newItem) {
        return oldItem.equals(newItem);
    }

    @Override
    public boolean areContentsTheSame(T oldItem, T newItem) {
        return true;
    }
}