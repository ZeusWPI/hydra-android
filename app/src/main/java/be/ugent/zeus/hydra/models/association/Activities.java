package be.ugent.zeus.hydra.models.association;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by feliciaan on 27/01/16.
 */

public class Activities extends ArrayList<Activity> {

    /**
     * Filter function for the events of different associations.
     *
     * This version is static because in a lot of cases we don't have access to this class, and only to the ArrayList
     * class. While not optimal, this is a consequence of the type erasure of Java. This should be solved in Java 9, so
     * we'll probably see it in Android in 2050 or so.
     *
     * @param data The data to filter. This list is not touched.
     * @param context The context.
     * @return The result of the filtering. Note that it is not defined if a copy is returned or not, so if you need
     *         to retain the original array, you need to make a copy yourself.
     */
    public static List<Activity> getPreferredActivities(List<Activity> data, Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        boolean filter = sharedPrefs.getBoolean("pref_association_checkbox", false);

        if (filter) {
            ArrayList<Activity> preferred = new ArrayList<>();

            for (Activity asso : data) {
                if (sharedPrefs.getBoolean(asso.getAssociation().getName(), false)) {
                    preferred.add(asso);
                }
            }

            return preferred;
        } else {
            return data;
        }

    }
}
