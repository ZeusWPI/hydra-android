package be.ugent.zeus.hydra.loaders.plugin;

import android.os.Bundle;
import android.support.v4.content.Loader;

import be.ugent.zeus.hydra.loaders.LoaderResult;

/**
 * Simple interface to provide a {@link Loader}.
 *
 * @author Niko Strijbol
 */
@FunctionalInterface
public interface LoaderProvider<D> {

    /**
     * Equivalent of {@link android.support.v4.app.LoaderManager.LoaderCallbacks#getLoader(int)},
     * but simplified. The behavior of this method must be equivalent to the one above. This method is for use with
     * one loader, as it does not support ids.
     *
     * @return The loader.
     */
    Loader<LoaderResult<D>> getLoader(int id, Bundle args);
}