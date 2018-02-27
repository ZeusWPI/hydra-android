package be.ugent.zeus.hydra.association.event;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import be.ugent.zeus.hydra.association.preference.AssociationSelectPrefActivity;
import java8.lang.Iterables;
import java8.util.function.Function;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Filters events according to the user preferences.
 *
 * @author Niko Strijbol
 */
class DisabledEventRemover implements Function<List<Event>, List<Event>> {

    private final SharedPreferences preferences;

    DisabledEventRemover(Context context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public List<Event> apply(List<Event> events) {
        // Not all list support editing, so make a copy.
        events = new ArrayList<>(events);
        // The set of disabled associations.
        Set<String> disabled = preferences.getStringSet(AssociationSelectPrefActivity.PREF_ASSOCIATIONS_SHOWING, Collections.emptySet());
        // Remove events that have are of the disabled associations.
        Iterables.removeIf(events, e -> disabled.contains(e.getAssociation().getInternalName()));
        return events;
    }
}