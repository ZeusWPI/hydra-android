package be.ugent.zeus.hydra.ui.main.events;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import be.ugent.zeus.hydra.data.models.association.Event;
import be.ugent.zeus.hydra.data.network.requests.Requests;
import be.ugent.zeus.hydra.data.network.requests.association.EventRequest;
import be.ugent.zeus.hydra.data.network.requests.association.FilteredEventRequest;
import be.ugent.zeus.hydra.repository.data.RefreshingLiveData;
import be.ugent.zeus.hydra.ui.preferences.AssociationSelectPrefActivity;

import java.util.Arrays;
import java.util.List;

/**
 * Live data for events that will listen to changes in the preferences for the filtering.
 *
 * @author Niko Strijbol
 */
public class EventLiveData extends RefreshingLiveData<List<Event>> implements SharedPreferences.OnSharedPreferenceChangeListener {

    public EventLiveData(Context context) {
        super(context, Requests.map(Requests.map(
                Requests.cache(context, new EventRequest()), Arrays::asList),
                FilteredEventRequest.transformer(context))
        );
    }

    @Override
    protected void onActive() {
        super.onActive();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        // We want to receive updates even if nothing is active, since the preferences are updated
        // in another activity.
        if (!hasObservers()) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            preferences.unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (AssociationSelectPrefActivity.PREF_ASSOCIATIONS_SHOWING.equals(key)) {
            loadData(Bundle.EMPTY);
        }
    }
}