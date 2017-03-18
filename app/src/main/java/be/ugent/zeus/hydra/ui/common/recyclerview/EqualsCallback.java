package be.ugent.zeus.hydra.ui.common.recyclerview;

import android.support.v7.util.DiffUtil;

import java.util.List;

/**
 * Default callback for {@link DiffUtil}, using the equals method.
 *
 * @author Niko Strijbol
 */
public class EqualsCallback extends DiffUtil.Callback {

    private final List<?> oldItems;
    private final List<?> newItems;

    /**
     * @param oldItems The old items. May not contain null-elements.
     * @param newItems The new items. May not contain null-elements.
     */
    public EqualsCallback(List<?> oldItems, List<?> newItems) {
        this.oldItems = oldItems;
        this.newItems = newItems;
    }

    @Override
    public int getOldListSize() {
        return oldItems.size();
    }

    @Override
    public int getNewListSize() {
        return newItems.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldItems.get(oldItemPosition).equals(newItems.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return true;
    }
}