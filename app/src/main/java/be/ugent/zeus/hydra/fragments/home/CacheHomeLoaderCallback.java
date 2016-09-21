package be.ugent.zeus.hydra.fragments.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;

import be.ugent.zeus.hydra.caching.CacheRequest;
import be.ugent.zeus.hydra.loaders.RequestAsyncTaskLoader;
import be.ugent.zeus.hydra.loaders.ThrowableEither;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.requests.common.ProcessableCacheRequest;

import java.io.Serializable;
import java.util.List;

/**
 * Base callback class for the loaders in the home fragment.
 *
 * @author Niko Strijbol
 */
abstract class CacheHomeLoaderCallback<D extends Serializable> extends HomeLoaderCallback {

    public CacheHomeLoaderCallback(Context context, HomeCardAdapter adapter, FragmentCallback callback) {
        super(context, adapter, callback);
    }

    @Override
    public Loader<ThrowableEither<List<HomeCard>>> onCreateLoader(int id, Bundle args) {
        return new RequestAsyncTaskLoader<>(new HomeRequest(context, getCacheRequest(), callback.shouldRefresh()), context);
    }

    /**
     * Convert the loaded data to a list of home cards. This method may be called in a different thread.
     *
     * @param data The loaded data or null if nothing needs to happen.
     * @return The converted data.
     */
    protected abstract List<HomeCard> convertData(@NonNull D data);

    /**
     * @return The request to execute.
     */
    protected abstract CacheRequest<D> getCacheRequest();

    private class HomeRequest extends ProcessableCacheRequest<D, List<HomeCard>> {

        private HomeRequest(Context context, CacheRequest<D> request, boolean shouldRefresh) {
            super(context, request, shouldRefresh);
        }

        @NonNull
        @Override
        protected List<HomeCard> transform(@NonNull D data) {
            return convertData(data);
        }
    }
}