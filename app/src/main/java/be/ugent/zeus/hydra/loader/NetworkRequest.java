package be.ugent.zeus.hydra.loader;

import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.loader.cache.Cache;
import be.ugent.zeus.hydra.loader.cache.exceptions.RequestFailureException;

/**
 * @author Niko Strijbol
 * @version 4/07/2016
 */
public interface NetworkRequest<T> {

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
