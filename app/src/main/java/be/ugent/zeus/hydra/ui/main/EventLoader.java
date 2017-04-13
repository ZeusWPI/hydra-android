package be.ugent.zeus.hydra.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import be.ugent.zeus.hydra.data.models.association.Event;
import be.ugent.zeus.hydra.data.network.CachedRequest;
import be.ugent.zeus.hydra.data.network.ListRequest;
import be.ugent.zeus.hydra.data.network.requests.association.EventRequest;
import be.ugent.zeus.hydra.data.network.requests.association.FilteredEventRequest;
import be.ugent.zeus.hydra.ui.common.loaders.PreferenceListener;
import be.ugent.zeus.hydra.ui.common.loaders.RequestAsyncTaskLoader;
import be.ugent.zeus.hydra.ui.preferences.AssociationSelectPrefActivity;

import java.util.List;

/**
 * Loader for {@link Event}s. This loader will listen to the options and refresh when they have changed.
 *
 * @author Niko Strijbol
 */
class EventLoader extends RequestAsyncTaskLoader<List<Event>> {

    private PreferenceListener preferenceListener;

    EventLoader(Context c, boolean refresh) {
        super(c, new FilteredEventRequest(c, new ListRequest<>(new CachedRequest<>(c, new EventRequest(), refresh))));
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        // Listen to changes
        SharedPreferences manager = PreferenceManager.getDefaultSharedPreferences(getContext());
        preferenceListener = new PreferenceListener(this, AssociationSelectPrefActivity.PREF_ASSOCIATIONS_SHOWING);
        manager.registerOnSharedPreferenceChangeListener(preferenceListener);
    }

    @Override
    protected void onReset() {
        super.onReset();
        if (preferenceListener != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            preferences.unregisterOnSharedPreferenceChangeListener(preferenceListener);
            preferenceListener = null;
        }
    }
}