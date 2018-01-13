package be.ugent.zeus.hydra.association.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import be.ugent.zeus.hydra.association.Event;
import be.ugent.zeus.hydra.ui.preferences.AssociationSelectPrefActivity;
import java8.util.Comparators;
import java8.util.function.Function;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Niko Strijbol
 */
public class EventFilter implements Function<List<Event>, List<Event>> {

    private final SharedPreferences preferences;

    public EventFilter(Context context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public List<Event> apply(List<Event> events) {
        Set<String> disabled = preferences.getStringSet(AssociationSelectPrefActivity.PREF_ASSOCIATIONS_SHOWING, Collections.emptySet());
        return StreamSupport.stream(events)
                .filter(e -> !disabled.contains(e.getAssociation().getInternalName()))
                .sorted(Comparators.comparing(Event::getStart))
                .collect(Collectors.toList());
    }
}