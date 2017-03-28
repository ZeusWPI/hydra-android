package be.ugent.zeus.hydra.data.network;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.network.exceptions.RequestFailureException;

/**
 * The basis interface for a request. A request is something that returns data, not unlike a AsyncTask, but without
 * any constraints. It is basically a replacement for Supplier<T> in Java 8.
 *
 * @author Niko Strijbol
 */
@FunctionalInterface
public interface Request<T> {

    /**
     * Perform the request.
     *
     * @return The data.
     *
     * @throws RequestFailureException This exception is thrown whenever an exception occurs while getting the data. For
     *                                 example, a netwerk failure while accessing an API.
     */
    @NonNull
    T performRequest() throws RequestFailureException;
}