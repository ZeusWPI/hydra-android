package be.ugent.zeus.hydra.association;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Manage the association preferences.
 * <p>
 * Normally we store a whitelist of associations we want to show. However, if we haven't initiated the whitelist yet,
 * this would result in no associations being loaded at all. To prevent this, we have a special flag indicating if the
 * whitelist has been initiated or not.
 *
 * @author Niko Strijbol
 */
public final class AssociationStore {

    private AssociationStore() {
        // No.
    }

    /**
     * Key for the preference that contains which associations should be shown.
     */
    public static final String PREF_WHITELIST = "pref_associations_whitelist";

    /**
     * Read the whitelist and initialise it if needed. This method will always return the whitelist.
     *
     * @param context The context to access the preferences.
     * @param map     The association map for initialisation.
     * @return The whitelist (or all associations if initiated). The resulting set is a copy, and may be modified,
     * although changes are not automatically saved (since it's a copy).
     */
    @NonNull
    public static Set<String> read(@NonNull Context context, @NonNull AssociationMap map) {
        Set<String> initial = read(context);
        if (initial == null) {
            Set<String> associations = map.associations().map(Association::getAbbreviation).collect(Collectors.toSet());
            replace(context, associations);
            return associations;
        } else {
            return initial;
        }
    }

    /**
     * Read the whitelist. If you have access to an association map, prefer {@link #read(Context, AssociationMap)},
     * which doesn't return {@code null}. If the whitelist is not initialised, you may not perform filtering.
     *
     * @param context The context for accessing the preferences.
     * @return The whitelist or null of not initialised.
     */
    @Nullable
    public static Set<String> read(@NonNull Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> stored = preferences.getStringSet(PREF_WHITELIST, null);
        if (stored == null) {
            return null;
        } else {
            return new HashSet<>(stored);
        }
    }

    /**
     * Blacklist one association. This will only have effect if the whitelist
     * has been initialised, otherwise the change is lost.
     *
     * @param context      The context for accessing the preferences.
     * @param abbreviation The abbreviation of the association you want to whitelist.
     */
    public static void blacklist(@NonNull Context context, @NonNull String abbreviation) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> existing = read(context);
        if (existing != null) {
            existing.remove(abbreviation);
            preferences.edit()
                    .putStringSet(PREF_WHITELIST, existing)
                    .apply();
        }
    }

    /**
     * Whitelist one association. This will only have effect if the whitelist
     * has been initialised, otherwise the change is lost.
     *
     * @param context      The context for accessing the preferences.
     * @param abbreviation The abbreviation of the association you want to whitelist.
     */
    public static void whitelist(@NonNull Context context, @NonNull String abbreviation) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> existing = read(context);
        if (existing != null) {
            existing.add(abbreviation);
            preferences.edit()
                    .putStringSet(PREF_WHITELIST, existing)
                    .apply();
        }
    }

    /**
     * Replace the whitelist. This will also cause the whitelist to be initiated.
     *
     * @param context   The context for accessing the preferences.
     * @param whitelist The whitelist.
     */
    public static void replace(@NonNull Context context, @NonNull Set<String> whitelist) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit()
                .putStringSet(PREF_WHITELIST, whitelist)
                .apply();
    }
}
