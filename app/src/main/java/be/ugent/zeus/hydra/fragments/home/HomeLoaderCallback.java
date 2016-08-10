package be.ugent.zeus.hydra.fragments.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.Loader;
import android.support.v7.preference.PreferenceManager;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.cache.CacheRequest;
import be.ugent.zeus.hydra.loader.LoaderCallback;
import be.ugent.zeus.hydra.loader.ThrowableEither;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.requests.common.ProcessedCacheableRequest;

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
    protected final FragmentCallback callback;

    public HomeLoaderCallback(Context context, HomeCardAdapter adapter, FragmentCallback callback) {
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

        String e = this.context.getString(R.string.fragment_home_error);
        String name = this.context.getString(getErrorName());

        callback.onError(String.format(e, name));
    }

    /**
     * Convert the loaded data to a list of home cards. This method may be called in a different thread.
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
     * @return Name of this request for error messages.
     */
    @StringRes
    protected abstract int getErrorName();

    /**
     * {@inheritDoc}
     *
     * This implementation wraps the request in a new request that also prepares the data in the background.
     *
     * Implementations should implement {@link #getCacheRequest()} most of the time and leave this alone.
     */
    @Override
    public CacheRequest<D, List<HomeCard>> getRequest() {
        return new ProcessedCacheableRequest<D, List<HomeCard>>(getCacheRequest()) {
            @NonNull
            @Override
            public List<HomeCard> getData(@NonNull D data) {
                return convertData(data);
            }
        };
    }
}