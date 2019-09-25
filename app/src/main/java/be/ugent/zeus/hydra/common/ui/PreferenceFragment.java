package be.ugent.zeus.hydra.common.ui;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

/**
 * Common fragment for preferences.
 *
 * @author Niko Strijbol
 */
public abstract class PreferenceFragment extends PreferenceFragmentCompat {

    @NonNull
    protected <T extends Preference> T requirePreference(String preference) {
        T result = findPreference(preference);
        if (result == null) {
            throw new NullPointerException("Preference does not exist, or is loaded too early.");
        } else {
            return result;
        }
    }
}
