package be.ugent.zeus.hydra.fragments.preferences;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;

/**
 * @author Niko Strijbol
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

