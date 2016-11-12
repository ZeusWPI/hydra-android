package be.ugent.zeus.hydra.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import be.ugent.zeus.hydra.fragments.home.HomeFeedFragment;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Niko Strijbol
 */
public class PreferencesUtils {

    public static void addToStringSet(Context context, String key, String value) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        Set<String> disabled = preferences.getStringSet(HomeFeedFragment.PREF_DISABLED_CARDS, Collections.<String>emptySet());

        Set<String> newDisabled = new HashSet<>(disabled);
        newDisabled.add(value);

        preferences.edit().putStringSet(HomeFeedFragment.PREF_DISABLED_CARDS, newDisabled).apply();
    }

}
