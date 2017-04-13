package be.ugent.zeus.hydra.ui.preferences;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;

/**
 * Preferences for the resto notification.
 *
 * @author Rien Maertens
 */
public class RestoPreferenceFragment extends PreferenceFragment {

    public static final String PREF_RESTO = "pref_resto_choice";
    public static final String PREF_DEFAULT_RESTO = "0";

    public static final String DEFAULT_CLOSING_TIME = "21:00";
    public static final String PREF_RESTO_CLOSING_HOUR = "pref_resto_closing_hour";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_resto);
    }

    @Override
    public void onResume() {
        super.onResume();
        HydraApplication.getApplication(getActivity()).sendScreenName("Settings > Resto");
    }
}