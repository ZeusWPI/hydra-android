package be.ugent.zeus.hydra.events;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import be.ugent.zeus.hydra.activities.preferences.AssociationSelectPrefActivity;
import be.ugent.zeus.hydra.models.association.Events;
import be.ugent.zeus.hydra.requests.RequestAsyncTaskLoader;
import be.ugent.zeus.hydra.requests.common.Request;

/**
 * @author Niko Strijbol
 */
public class EventLoader extends RequestAsyncTaskLoader<Events> {

    private PreferenceListener preferenceListener;

    /**
     * @param context The context.
     * @param request The request to get the data from.
     */
    public EventLoader(Context context, Request<Events> request) {
        super(context, request);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        // Listen to changes
        SharedPreferences manager = PreferenceManager.getDefaultSharedPreferences(getContext());
        preferenceListener = new PreferenceListener();
        manager.registerOnSharedPreferenceChangeListener(preferenceListener);
    }

    @Override
    protected void onReset() {
        super.onReset();
        // Stop listening
        if (preferenceListener != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            preferences.unregisterOnSharedPreferenceChangeListener(preferenceListener);
            preferenceListener = null;
        }
    }

    private class PreferenceListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(AssociationSelectPrefActivity.PREF_ASSOCIATIONS_SHOWING)) {
                onContentChanged();
            }
        }
    }
}