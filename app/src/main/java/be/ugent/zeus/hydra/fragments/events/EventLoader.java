package be.ugent.zeus.hydra.fragments.events;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import be.ugent.zeus.hydra.activities.preferences.AssociationSelectPrefActivity;
import be.ugent.zeus.hydra.loaders.changes.PreferenceListener;
import be.ugent.zeus.hydra.models.association.Events;
import be.ugent.zeus.hydra.requests.RequestAsyncTaskLoader;
import be.ugent.zeus.hydra.requests.association.FilteredEventRequest;
import be.ugent.zeus.hydra.requests.common.Request;

/**
 * Loader for {@link Events}. This loader will listen to the options and refresh when they have changed.
 *
 * @author Niko Strijbol
 */
public class EventLoader extends RequestAsyncTaskLoader<Events> {

    private PreferenceListener preferenceListener;

    public EventLoader(Context context, Request<Events> request) {
        super(context, request);
    }

    public static EventLoader filteredEvents(boolean refresh, Context context) {
        return new EventLoader(context, new FilteredEventRequest(context, refresh));
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