package be.ugent.zeus.hydra.requests.common;

import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.loader.cache.Cache;
import be.ugent.zeus.hydra.loader.cache.exceptions.RequestFailureException;

/**
 * The basis interface for a request. A request is something that returns data, not unlike a AsyncTask, but without
 * any constraints. It is basically a replacement for Supplier<T> in Java 8.
 *
 * @author Niko Strijbol
 */
public interface Request<T> {

    /**
     * Perform the request. This method provides the data that may or may not be cached, depending on the implementation
     * of the used {@link Cache}.
     *
     * @return The data.
     *
     * @throws RequestFailureException This exception is thrown whenever an exception occurs while getting the data.
     *  For example, a netwerk failure while accessing an API.
     */
    @NonNull
    T performRequest() throws RequestFailureException;
}