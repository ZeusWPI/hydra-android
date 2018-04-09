package be.ugent.zeus.hydra.common.ui.recyclerview.adapters;

import android.support.annotation.Nullable;

import java.util.List;

/**
 * Dumb update, where no information about the data update is available.
 *
 * @author Niko Strijbol
 */
class DumbUpdate<D> implements AdapterUpdate<D> {

    private final List<D> newData;

    DumbUpdate(List<D> newData) {
        this.newData = newData;
    }

    @Nullable
    @Override
    public List<D> getNewData(@Nullable List<D> existingData) {
        return newData;
    }

    @Override
    public void applyUpdatesTo(ListUpdateCallback listUpdateCallback) {
        listUpdateCallback.onDataSetChanged();
    }

    @Override
    public boolean shouldUseMultiThreading() {
        return false;
    }
}