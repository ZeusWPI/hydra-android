package be.ugent.zeus.hydra.fragments.common;

import android.os.Bundle;
import android.support.v4.content.Loader;

import be.ugent.zeus.hydra.loader.CachedAsyncTaskLoader;
import be.ugent.zeus.hydra.loader.ErrorLoaderCallback;
import be.ugent.zeus.hydra.loader.ThrowableEither;

import java.io.Serializable;

/**
 * Fragment that uses the {@link be.ugent.zeus.hydra.loader.CachedAsyncTaskLoader}.
 *
 * The fragment supports a progress bar and refresh. The progress bar is automatically hidden. This fragment is
 * mostly used in the home screen.
 *
 * @author Niko Strijbol
 */
public abstract class CachedLoaderFragment<D extends Serializable> extends LoaderFragment<D> implements ErrorLoaderCallback<D> {

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     *
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<ThrowableEither<D>> onCreateLoader(int id, Bundle args) {
        return new CachedAsyncTaskLoader<>(getRequest(), getContext(), shouldRenew);
    }
}