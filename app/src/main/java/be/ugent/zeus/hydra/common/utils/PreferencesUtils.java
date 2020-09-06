package be.ugent.zeus.hydra.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Niko Strijbol
 */
public class PreferencesUtils {

    public static void addToStringSet(Context context, String key, String value) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        Set<String> newSet = new HashSet<>(preferences.getStringSet(key, Collections.emptySet()));
        newSet.add(value);

        preferences.edit().putStringSet(key, newSet).apply();
    }

    public static void removeFromStringSet(Context context, String key, String value) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        Set<String> newSet = new HashSet<>(preferences.getStringSet(key, Collections.emptySet()));
        newSet.remove(value);

        preferences.edit().putStringSet(key, newSet).apply();
    }

    /**
     * Get a string set from the preferences, with an empty set as default.
     *
     * @param context The context to get the preferences from.
     * @param key     The preferences key.
     * @return The set.
     */
    @NonNull
    public static Set<String> getStringSet(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return new HashSet<>(preferences.getStringSet(key, Collections.emptySet()));
    }

    public static boolean isSetIn(int flagSet, int flag) {
        return (flagSet & flag) == flag;
    }
}
