package be.ugent.zeus.hydra.fragments.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;

import be.ugent.zeus.hydra.cache.CacheRequest;
import be.ugent.zeus.hydra.cache.CachedAsyncTaskLoader;
import be.ugent.zeus.hydra.loader.ThrowableEither;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.requests.common.ProcessedCacheableRequest;

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
        return new CachedAsyncTaskLoader<>(
                new ProcessedCacheableRequest<D, List<HomeCard>>(getCacheRequest()) {
                    @NonNull
                    @Override
                    public List<HomeCard> getData(@NonNull D data) {
                        return convertData(data);
                    }
                },
                context, callback.shouldRefresh());
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
    protected abstract CacheRequest<D, D> getCacheRequest();
}