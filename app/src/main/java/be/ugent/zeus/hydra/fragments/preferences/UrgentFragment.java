package be.ugent.zeus.hydra.fragments.preferences;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import be.ugent.zeus.hydra.R;

/**
 * Preferences for Urgent things.
 *
 * @author Niko Strijbol
 */
public class UrgentFragment extends PreferenceFragment {

    public static final String PREF_URGENT_USE_LOW_QUALITY = "pref_urgent_use_low_quality";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_urgent);
    }
}