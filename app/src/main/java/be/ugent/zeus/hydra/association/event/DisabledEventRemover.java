package be.ugent.zeus.hydra.association.event;

import android.content.Context;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.common.utils.PreferencesUtils;

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
        Set<String> disabled = PreferencesUtils.getStringSet(context, PREF_ASSOCIATIONS_SHOWING).stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        return events.stream()
                .filter(event -> !disabled.contains(event.getAssociation().toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
    }
}
