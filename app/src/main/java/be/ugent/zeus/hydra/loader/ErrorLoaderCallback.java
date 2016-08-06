package be.ugent.zeus.hydra.loader;

import android.support.v4.app.LoaderManager;

import be.ugent.zeus.hydra.cache.CacheRequest;
import be.ugent.zeus.hydra.requests.common.RequestExecutor;

/**
 * Defines additional methods a class should implement to work with {@link ThrowableEither} as a response.
 * Note that this is not at all necessary, but may be a guide for correct implementation.
 *
 * When we can finally use default methods, this interface will provide a default implementation for the
 * LoaderCallback.
 *
 * @author Niko Strijbol
 */
public interface ErrorLoaderCallback<D> extends LoaderManager.LoaderCallbacks<ThrowableEither<D>>, RequestExecutor.Callback<D> {

    /**
     * @return The request that will be executed.
     */
    CacheRequest<D> getRequest();
}