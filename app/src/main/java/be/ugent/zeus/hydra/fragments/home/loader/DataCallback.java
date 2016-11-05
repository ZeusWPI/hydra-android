package be.ugent.zeus.hydra.fragments.home.loader;

import android.support.annotation.UiThread;
import android.support.v7.util.DiffUtil;

/**
 * @author Niko Strijbol
 */
public interface DataCallback<D> {

    /**
     * Receive an update. The indented use is as follows:
     *
     * 1. Assign the received data as adapter data.
     * 2. Execute the update on the adapter.
     *
     * @param data The new data. This is a copy of the data, and it is sorted.
     * @param update The updates for the adapter.
     */
    @UiThread
    void onDataUpdated(D data, DiffUtil.DiffResult update);

    /**
     * Get the current data. The current data is needed to calculate the difference between that data, and
     * the new data that will be received using {@link #onDataUpdated(Object, DiffUtil.DiffResult)}.
     *
     * The returned list must not be modified, so it is not necessary to return a copy.
     *
     * @return The current data.
     */
    D getCurrentList();
}