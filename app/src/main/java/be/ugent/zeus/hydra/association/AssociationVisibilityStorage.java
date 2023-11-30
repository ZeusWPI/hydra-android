/*
 * Copyright (c) 2022 Niko Strijbol
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra.association;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class that manages storing which associations the user wants to see
 * and which they don't.
 * <p>
 * Internally, it works with a whitelist that is updated to add new
 * associations if needed.
 *
 * @author Niko Strijbol
 */
public class AssociationVisibilityStorage {

    private AssociationVisibilityStorage() {
        // No.
    }

    /**
     * Key for the preference that contains which associations should be shown.
     */
    public static final String PREF_BLACKLIST = "pref_associations_blacklist";

    /**
     * Calculate the whitelist, i.e. the associations the user does want to see.
     * <p>
     * This function is intended to be called after an association request has
     * been made, but before an event request has been made. As such, it will
     * also update the blacklist by removing associations that no longer exist
     * on the server. In practice, this shouldn't be an issue, but we want to
     * do the right thing™.
     *
     * @param context      The context.
     * @param associations An unfiltered list of associations from the server.
     * @param newWhitelist An updated whitelist if available.
     * @return The set of association abbreviations we want to see.
     */
    public static Set<String> calculateWhitelist(Context context, List<Association> associations, @Nullable List<Pair<Association, Boolean>> newWhitelist) {
        // Start with all associations.
        Set<String> whitelist = new HashSet<>();
        for (Association association : associations) {
            whitelist.add(association.abbreviation());
        }

        // Get the existing blacklist, either from storage or from the selected one.
        Set<String> blacklist;
        if (newWhitelist == null) {
            blacklist = blacklist(context);

            // Remove any obsolete associations.
            Set<String> obsolete = new HashSet<>();
            for (String blacklisted : blacklist) {
                if (!whitelist.remove(blacklisted)) {
                    obsolete.add(blacklisted);
                }
            }

            // Remove obsolete items from the saved blacklist.
            blacklist.removeAll(obsolete);
        } else {
            blacklist = newWhitelist.stream()
                    .filter(p -> !p.second)
                    .map(p -> p.first.abbreviation())
                    .collect(Collectors.toCollection(HashSet::new));
        }
        
        whitelist.removeAll(blacklist);

        // Save the blacklist.
        saveBlacklist(context, blacklist);

        return whitelist;
    }

    /**
     * Get the saved blacklist.
     */
    @NonNull
    public static Set<String> blacklist(@NonNull Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return new HashSet<>(preferences.getStringSet(PREF_BLACKLIST, new HashSet<>()));
    }

    /**
     * Blacklist one association. This will only have effect if the whitelist
     * has been initialised, otherwise the change is lost.
     *
     * @param context      The context for accessing the preferences.
     * @param abbreviation The abbreviation of the association you want to whitelist.
     */
    public static void blacklist(@NonNull Context context, @NonNull String abbreviation) {
        Set<String> existing = blacklist(context);
        existing.add(abbreviation);
        saveBlacklist(context, existing);
    }

    /**
     * Whitelist one association. This will only have effect if the whitelist
     * has been initialised, otherwise the change is lost.
     *
     * @param context      The context for accessing the preferences.
     * @param abbreviation The abbreviation of the association you want to whitelist.
     */
    public static void whitelist(@NonNull Context context, @NonNull String abbreviation) {
        Set<String> existing = blacklist(context);
        existing.remove(abbreviation);
        saveBlacklist(context, existing);
    }

    /**
     * Get the saved blacklist.
     */
    private static void saveBlacklist(@NonNull Context context, Set<String> blacklist) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit()
                .putStringSet(PREF_BLACKLIST, blacklist)
                .apply();
    }
}
