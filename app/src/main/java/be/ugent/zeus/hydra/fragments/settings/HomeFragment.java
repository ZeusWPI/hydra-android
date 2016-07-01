package be.ugent.zeus.hydra.fragments.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;

/**
 * @author Rien Maertens
 * @since 16/02/2016.
 *
 *
SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
boolean sf = sharedPrefs.getBoolean("pref_association_checkbox", false);
 */
public class HomeFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.home);
    }

    @Override
    public void onResume() {
        super.onResume();

        HydraApplication happ = (HydraApplication) getActivity().getApplication();
        happ.sendScreenName("settings");
    }
}

