package be.ugent.zeus.hydra.association.event;

import android.content.Context;

import be.ugent.zeus.hydra.utils.PreferencesUtils;
import java9.util.function.Function;
import java9.util.stream.Collectors;
import java9.util.stream.StreamSupport;

import java.util.List;
import java.util.Set;

import static be.ugent.zeus.hydra.association.preference.AssociationSelectionPreferenceFragment.PREF_ASSOCIATIONS_SHOWING;

/**
 * Filters events according to the user preferences.
 *
 * @author Niko Strijbol
 */
class DisabledEventRemover implements Function<List<Event>, List<Event>> {

    private final Context context;

    DisabledEventRemover(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public List<Event> apply(List<Event> events) {
        Set<String> disabled = StreamSupport.stream(PreferencesUtils.getStringSet(context, PREF_ASSOCIATIONS_SHOWING))
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        return StreamSupport.stream(events)
                .filter(event -> !disabled.contains(event.getAssociation().getInternalName().toLowerCase()))
                .collect(Collectors.toList());
    }
}