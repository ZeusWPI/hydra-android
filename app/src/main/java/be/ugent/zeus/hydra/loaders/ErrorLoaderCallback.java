package be.ugent.zeus.hydra.loaders;

import android.support.v4.app.LoaderManager;

import be.ugent.zeus.hydra.caching.CacheRequest;
import be.ugent.zeus.hydra.requests.executor.RequestCallback;

/**
 * Defines additional methods a class should implement to work with {@link ThrowableEither} as a response.
 * Note that this is not at all necessary, but may be a guide for correct implementation.
 *
 * @author Niko Strijbol
 */
public interface ErrorLoaderCallback<D> extends LoaderManager.LoaderCallbacks<ThrowableEither<D>>, RequestCallback<D> {

    /**
     * @return The request that will be executed.
     */
    CacheRequest<D> getRequest();
}