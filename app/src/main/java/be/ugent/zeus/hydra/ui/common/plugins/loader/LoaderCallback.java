package be.ugent.zeus.hydra.ui.common.plugins.loader;

import android.os.Bundle;
import android.support.v4.content.Loader;

import be.ugent.zeus.hydra.ui.common.loaders.LoaderResult;

/**
 * Simple interface to provide a {@link Loader}.
 *
 * @author Niko Strijbol
 */
@FunctionalInterface
public interface LoaderCallback<D> {

    /**
     * Equivalent of {@link android.support.v4.app.LoaderManager.LoaderCallbacks#getLoader(int)},
     * but simplified. The behavior of this method must be equivalent to the one above. This method is for use with
     * one loader, as it does not support ids.
     *
     * @return The loader.
     */
    Loader<LoaderResult<D>> getLoader(Bundle args);
}