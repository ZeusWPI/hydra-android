package be.ugent.zeus.hydra.models.association;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import be.ugent.zeus.hydra.activities.preferences.AssociationSelectPrefActivity;

import java.util.*;

/**
 * Gson helper class.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class Events extends ArrayList<Event> {

    /**
     * Filter function for the events of different associations. This function works by modifying the given list.
     *
     * This version is static because in a lot of cases we don't have access to this class, and only to the ArrayList
     * class. While not optimal, this is a consequence of the type erasure of Java. This should be solved in Java 9, so
     * we'll probably see it in Android in 2050 or so.
     *
     * @param data The data to filter.
     * @param context The context.
     */
    public static List<Event> filterEvents(List<Event> data, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> disabled = preferences.getStringSet(AssociationSelectPrefActivity.PREF_ASSOCIATIONS_SHOWING, new HashSet<>());

        //Why no filter :(
        Iterator<Event> iterator = data.iterator();
        while(iterator.hasNext()) {
            if(disabled.contains(iterator.next().getAssociation().getInternalName())) {
                iterator.remove();
            }
        }

        return data;
    }
}
