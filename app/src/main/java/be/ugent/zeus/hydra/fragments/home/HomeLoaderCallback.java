package be.ugent.zeus.hydra.fragments.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;
import android.support.v7.preference.PreferenceManager;

import be.ugent.zeus.hydra.cache.CacheRequest;
import be.ugent.zeus.hydra.cache.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.loader.LoaderCallback;
import be.ugent.zeus.hydra.loader.ThrowableEither;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Base callback class for the loaders in the home fragment.
 *
 * @author Niko Strijbol
 */
abstract class HomeLoaderCallback<D extends Serializable> extends LoaderCallback<D, List<HomeCard>> {

    protected final Context context;
    protected final HomeCardAdapter adapter;
    protected final ProgressCallback callback;

    public HomeLoaderCallback(Context context, HomeCardAdapter adapter, ProgressCallback callback) {
        this.context = context;
        this.adapter = adapter;
        this.callback = callback;
    }

    @Override
    public Loader<ThrowableEither<List<HomeCard>>> onCreateLoader(int id, Bundle args) {
        return super.onCreateLoader(this.context, callback.shouldRefresh());
    }

    @Override
    public void receiveData(@NonNull List<HomeCard> data) {

        if(!isTypeActive()) {
            return;
        }

        adapter.updateCardItems(data, getCardType());
        callback.onCompleted();
    }

    @Override
    public void receiveError(@NonNull Throwable error) {
        if(!isTypeActive()) {
            return;
        }
        callback.onError(getCardType());
    }

    /**
     * Convert the loaded data to a list of home cards. This method may be called in a different thread.
     *
     * TODO: should this be executed in a separate thread, or should we wrap the request in a new request that already
     * TODO: takes care of this. For now, we just do this on the main thread.
     *
     * @param data The loaded data or null if nothing needs to happen.
     * @return The converted data.
     */
    protected abstract List<HomeCard> convertData(@NonNull D data);

    /**
     * @return The card type of the cards that are produced here.
     */
    @HomeCard.CardType
    protected abstract int getCardType();

    /**
     * @return The request to execute.
     */
    protected abstract CacheRequest<D, D> getCacheRequest();

    /**
     * Check to see if a card type is showable.
     *
     * @return True if the card may be shown.
     */
    protected boolean isTypeActive() {
        Set<String> data = PreferenceManager.getDefaultSharedPreferences(this.context).getStringSet("pref_disabled_cards", Collections.<String>emptySet());
        return !data.contains(String.valueOf(getCardType()));
    }

    /**
     * {@inheritDoc}
     *
     * This implementation wraps the request in a new request that also prepares the data in the background.
     *
     * Implementations should implement {@link #getCacheRequest()} instead.
     */
    @Override
    public final CacheRequest<D, List<HomeCard>> getRequest() {

        return new CacheRequest<D, List<HomeCard>>() {

            private CacheRequest<D, D> request = getCacheRequest();

            @NonNull
            @Override
            public String getCacheKey() {
                return request.getCacheKey();
            }

            @Override
            public long getCacheDuration() {
                return request.getCacheDuration();
            }

            @NonNull
            @Override
            public List<HomeCard> getData(@NonNull D data) {
                return convertData(data);
            }

            @NonNull
            @Override
            public D performRequest() throws RequestFailureException {
                return request.performRequest();
            }
        };
    }

    public interface ProgressCallback {

        /**
         * Called when the loading was successfully completed.
         */
        void onCompleted();

        /**
         * Called when an error occurred during the loading.
         *
         * @param cardType The type of card that had a problem.
         */
        void onError(@HomeCard.CardType int cardType);

        boolean shouldRefresh();
    }
}