package be.ugent.zeus.hydra.common.ui.recyclerview.adapters;

import android.support.annotation.*;
import android.support.v7.util.ListUpdateCallback;

import java.util.List;

/**
 * Represents a data update for an {@link DiffAdapter}.
 */
public interface AdapterUpdate<D> {

    /**
     * Returns the new data. If you need to calculations, this is the place. Guaranteed to be called before
     * {@link #applyUpdatesTo(ListUpdateCallback)} is called.
     *
     * @param existingData The existing data. Will no longer be used after this call. Null if there is no existing data.
     * @return The new data. May be the same as the existing data. Null if no data is available.
     */
    @Nullable
    @AnyThread
    List<D> getNewData(@Nullable List<D> existingData);

    /**
     * Apply the update to the update callback. At this point, the underlying data of the callback is alreay
     * updated to reflect the new data.
     *
     * @param listUpdateCallback The callback to update.
     */
    @MainThread
    void applyUpdatesTo(ListUpdateCallback listUpdateCallback);

    /**
     * @return False if multithreading should not be used.
     */
    default boolean shouldUseMultiThreading() {
        return true;
    }
}