package be.ugent.zeus.hydra.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
}