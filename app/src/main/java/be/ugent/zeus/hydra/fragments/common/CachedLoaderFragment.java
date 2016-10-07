package be.ugent.zeus.hydra.fragments.common;

import android.support.v4.content.Loader;

import be.ugent.zeus.hydra.caching.CacheableRequest;
import be.ugent.zeus.hydra.loaders.RequestAsyncTaskLoader;
import be.ugent.zeus.hydra.loaders.ThrowableEither;
import be.ugent.zeus.hydra.requests.common.SimpleCacheRequest;

import java.io.Serializable;

/**
 * Fragment for {@link SimpleCacheRequest}.
 *
 * The fragment supports a progress bar and refresh. The progress bar is automatically hidden. This fragment is
 * mostly used in the home screen.
 *
 * @author Niko Strijbol
 */
public abstract class CachedLoaderFragment<D extends Serializable> extends LoaderFragment<D> {

    @Override
    public Loader<ThrowableEither<D>> getLoader() {
        return new RequestAsyncTaskLoader<>(new SimpleCacheRequest<>(getContext(), getRequest(), shouldRenew), getContext());
    }

    /**
     * @return The request that will be executed.
     */
    protected abstract CacheableRequest<D> getRequest();
}