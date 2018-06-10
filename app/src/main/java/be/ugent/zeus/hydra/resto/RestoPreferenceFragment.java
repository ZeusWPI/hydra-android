package be.ugent.zeus.hydra.resto;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.MainActivity;

/**
 * Preferences for the resto notification.
 *
 * @author Rien Maertens
 */
public class RestoPreferenceFragment extends PreferenceFragment {

    // We need the 2 suffix, because the original one is from an older system.
    /**
     * The key of the resto we want to show.
     */
    public static final String PREF_RESTO_KEY = "pref_resto_choice_2";
    /**
     * The name of the chosen resto. We keep this saved, as we then don't have to access to full file every time.
     */
    public static final String PREF_RESTO_NAME = "pref_resto_choice_name";

    public static final String DEFAULT_CLOSING_TIME = "21:00";
    public static final String PREF_RESTO_CLOSING_HOUR = "pref_resto_closing_hour";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_resto);

        findPreference("pref_choice_resto_select").setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra(MainActivity.ARG_TAB, R.id.drawer_resto);
            startActivity(intent);
            return true;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        HydraApplication.getApplication(getActivity()).sendScreenName("Settings > Resto");
    }

    public static String getDefaultResto(Context context) {
        return context.getString(R.string.value_resto_default_endpoint);
    }

    public static String getRestoEndpoint(Context context, SharedPreferences preferences) {
        String defaultResto = getDefaultResto(context);
        return preferences.getString(PREF_RESTO_KEY, defaultResto);
    }
}