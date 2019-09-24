package be.ugent.zeus.hydra.association.event.list;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import be.ugent.zeus.hydra.association.event.RawEventRequest;
import be.ugent.zeus.hydra.association.preference.AssociationSelectionPreferenceFragment;
import be.ugent.zeus.hydra.common.arch.data.RequestLiveData;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Live data for events that will listen to changes in the preferences for the filtering.
 *
 * @author Niko Strijbol
 */
class EventLiveData extends RequestLiveData<List<EventItem>> implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Set<String> disabledAssociations;

    EventLiveData(Context context) {
        super(context, RawEventRequest.cachedFilteredSortedRequest(context).map(new EventListConverter()));
    }

    @Override
    protected void onActive() {
        super.onActive();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Set<String> current = preferences.getStringSet(AssociationSelectionPreferenceFragment.PREF_ASSOCIATIONS_SHOWING, Collections.emptySet());
        // Register the listener for when the settings change while it's active
        preferences.registerOnSharedPreferenceChangeListener(this);
        // Check if the value is equal to the saved value. If not, we need to reload.
        if (disabledAssociations != null && !current.equals(disabledAssociations)) {
            loadData();
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
        if (AssociationSelectionPreferenceFragment.PREF_ASSOCIATIONS_SHOWING.equals(key)) {
            loadData();
        }
    }
}