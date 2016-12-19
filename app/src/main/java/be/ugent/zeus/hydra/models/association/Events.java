package be.ugent.zeus.hydra.models.association;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import be.ugent.zeus.hydra.activities.preferences.AssociationSelectPrefActivity;
import java8.util.function.Predicate;

import java.util.*;

/**
 * Gson helper class.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class Events extends ArrayList<Event> {

    public Events() {
        super();
    }

    public Events(Collection<? extends Event> c) {
        super(c);
    }

    /**
     * Returns a predicate that can be used to filter events according to the user preferences. This predicate will
     * match any event that is not disabled.
     *
     * @param context The context.
     *
     * @return The data.
     */
    public static Predicate<Event> filterEvents(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> disabled = preferences.getStringSet(AssociationSelectPrefActivity.PREF_ASSOCIATIONS_SHOWING, new HashSet<>());
        return e -> !disabled.contains(e.getAssociation().getInternalName());
    }
}