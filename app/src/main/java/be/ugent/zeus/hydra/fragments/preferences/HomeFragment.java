package be.ugent.zeus.hydra.fragments.preferences;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;

/**
 * Settings about the home feed.
 *
 * @author Niko Strijbol
 */
public class HomeFragment extends PreferenceFragment {

    public static final String PREF_DATA_SAVER = "pref_home_feed_save_data";
    public static final boolean PREF_DATA_SAVER_DEFAULT = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_home_feed);
    }

    @Override
    public void onResume() {
        super.onResume();
        HydraApplication.getApplication(getActivity()).sendScreenName("Settings > Home feed");
    }
}

