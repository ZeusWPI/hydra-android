package be.ugent.zeus.hydra.loaders;

import android.support.annotation.NonNull;

/**
 * Callback for the class that wants to receive data from a loader.
 *
 * @deprecated Use consumers instead.
 *
 * @author Niko Strijbol
 */
@Deprecated
public interface DataCallback<D> {

    /**
     * Receive the data if the request was completed successfully.
     *
     * @param data The data.
     */
    void receiveData(@NonNull D data);

    /**
     * Receive an error if the request failed for some reason.
     *
     * @param e The occurred exception.
     */
    void receiveError(@NonNull Throwable e);
}