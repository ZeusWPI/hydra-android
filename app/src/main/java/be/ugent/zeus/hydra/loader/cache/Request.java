package be.ugent.zeus.hydra.loader.cache;

import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.loader.cache.exceptions.RequestFailureException;

/**
 * Represents a cacheable request for data. This is the class that provides the data for the {@link Cache}.
 *
 * Note that there is no guarantee for the order in which the methods of this class are called, if called at all.
 *
 * If your cache key or cache duration depends on the result, one solution is the perform the request as soon as
 * one the of the methods is called, and saving the result locally in the class.
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

    /**
     * @return The key under which the result will be stored by the {@link Cache}.
     */
    @NonNull
    String getCacheKey();

    /**
     * @return The maximal duration the data should be cached.
     */
    long getCacheDuration();
}