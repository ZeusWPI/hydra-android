package be.ugent.zeus.hydra.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import be.ugent.zeus.hydra.R;

/**
 * @author Rien Maertens
 * @since 16/02/2016.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.layout.preferences);
    }
}
