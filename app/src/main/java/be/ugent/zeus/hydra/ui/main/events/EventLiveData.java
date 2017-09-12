package be.ugent.zeus.hydra.ui.main.events;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import be.ugent.zeus.hydra.data.network.requests.association.EventFilter;
import be.ugent.zeus.hydra.data.network.requests.association.EventRequest;
import be.ugent.zeus.hydra.repository.data.RequestLiveData;
import be.ugent.zeus.hydra.repository.requests.Requests;
import be.ugent.zeus.hydra.ui.preferences.AssociationSelectPrefActivity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Live data for events that will listen to changes in the preferences for the filtering.
 *
 * @author Niko Strijbol
 */
public class EventLiveData extends RequestLiveData<List<EventItem>> implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Set<String> disabledAssociations;

    public EventLiveData(Context context) {
        super(context, Requests.map(
                Requests.map(
                        Requests.map(Requests.cache(context, new EventRequest()), Arrays::asList),
                        new EventFilter(context)
                ),
                new EventItem.Converter()
        ));
    }

    @Override
    protected void onActive() {
        super.onActive();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Set<String> current = preferences.getStringSet(AssociationSelectPrefActivity.PREF_ASSOCIATIONS_SHOWING, Collections.emptySet());
        // Register the listener for when the settings change while it's active
        preferences.registerOnSharedPreferenceChangeListener(this);
        // Check if the value is equal to the saved value. If not, we need to reload.
        if (disabledAssociations != null && !current.equals(disabledAssociations)) {
            loadData(Bundle.EMPTY);
        }
        disabledAssociations = current;
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        preferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (AssociationSelectPrefActivity.PREF_ASSOCIATIONS_SHOWING.equals(key)) {
            loadData(Bundle.EMPTY);
        }
    }
}