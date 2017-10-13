package be.ugent.zeus.hydra.ui.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedFragment;

import java.util.Collections;

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

        findPreference("pref_home_feed_clickable")
                .setOnPreferenceClickListener(preference -> {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    preferences.edit().putStringSet(HomeFeedFragment.PREF_DISABLED_SPECIALS, Collections.emptySet()).apply();
                    Toast.makeText(getActivity(), R.string.pref_home_feed_cleared, Toast.LENGTH_SHORT).show();
                    return true;
                });

    }

    @Override
    public void onResume() {
        super.onResume();
        HydraApplication.getApplication(getActivity()).sendScreenName("Settings > Home feed");
    }
}

