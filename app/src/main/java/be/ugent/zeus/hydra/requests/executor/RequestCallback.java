package be.ugent.zeus.hydra.requests.executor;

import android.support.annotation.NonNull;

/**
 * Callback for the requests. This callback will be called
 *
 * @param <T> The result.
 *
 * @author Niko Strijbol
 */
public interface RequestCallback<T> {

    /**
     * Receive the data if the request was completed successfully.
     *
     * @param data The data.
     */
    void receiveData(@NonNull T data);

    /**
     * Receive an error if the request failed for some reason.
     *
     * @param e The occurred exception.
     */
    void receiveError(@NonNull Throwable e);
}