package be.ugent.zeus.hydra.common.ui.recyclerview.adapters;

import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v7.util.DiffUtil;

import java9.util.Objects;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Generic update for updated data.
 *
 * This class supports both adding data for the first time, updating data and removing all data.
 *
 * @author Niko Strijbol
 */
class DiffUpdate<D> implements AdapterUpdate<D> {

    private final DiffUtil.ItemCallback<D> callback;

    private final List<D> newData;

    private DiffUtil.DiffResult result;
    private int existingDataSize = -1;
    private final Set<Empty> status = EnumSet.noneOf(Empty.class);

    DiffUpdate(@Nullable List<D> newData) {
        this(new EqualsItemCallback<>(), newData);
    }

    DiffUpdate(DiffUtil.ItemCallback<D> callback, @Nullable List<D> newData) {
        this.newData = newData;
        this.callback = callback;
    }

    @Nullable
    @Override
    @WorkerThread
    public List<D> getNewData(@Nullable List<D> existingData) {

        if (existingData == null || existingData.isEmpty()) {
            status.add(Empty.OLD_DATA);
        } else {
            existingDataSize = existingData.size();
        }

        if (newData == null || newData.isEmpty()) {
            status.add(Empty.NEW_DATA);
        }

        if (status.isEmpty()) {
            assert existingData != null;
            assert newData != null;
            // Else we calculate a diff, as both are non-empty.
            result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return existingData.size();
                }

                @Override
                public int getNewListSize() {
                    return newData.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return callback.areItemsTheSame(existingData.get(oldItemPosition), newData.get(newItemPosition));
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return callback.areContentsTheSame(existingData.get(oldItemPosition), newData.get(newItemPosition));
                }
            }, true);
        }

        return newData;
    }

    @Override
    @MainThread
    public void applyUpdatesTo(ListUpdateCallback listUpdateCallback) {

        // Both are non-empty.
        if (status.isEmpty()) {
            Objects.requireNonNull(result).dispatchUpdatesTo(listUpdateCallback);
            return;
        }

        // Both are empty.
        if (status.containsAll(EnumSet.of(Empty.NEW_DATA, Empty.OLD_DATA))) {
            return;
        }

        // Only the new data is empty.
        if (status.contains(Empty.NEW_DATA)) {
            // Do remove.
            listUpdateCallback.onRemoved(0, existingDataSize);
            return;
        }

        // Only the old data is empty.
        if (status.contains(Empty.OLD_DATA)) {
            assert newData != null;
            listUpdateCallback.onInserted(0, newData.size());
            return;
        }

        throw new IllegalStateException("Illegal state in DataUpdate, one of the possibilities must happen.");
    }

    private enum Empty {
        NEW_DATA, OLD_DATA
    }
}