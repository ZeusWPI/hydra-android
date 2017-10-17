package be.ugent.zeus.hydra.ui.preferences;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.resto.menu.MenuActivity;

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
    public static final String PREF_DEFAULT_RESTO = "nl-debrug";

    public static final String DEFAULT_CLOSING_TIME = "21:00";
    public static final String PREF_RESTO_CLOSING_HOUR = "pref_resto_closing_hour";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_resto);

        findPreference("pref_choice_resto_select").setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(getActivity(), MenuActivity.class));
            return true;
        });

        //SelectableMetaViewModel metaViewModel = ViewModelProviders.of(getActivity()).get(SelectableMetaViewModel.class);
        //metaViewModel.getData().observe(this, SuccessObserver.with(this::receiveResto));
    }

    @Override
    public void onResume() {
        super.onResume();
        HydraApplication.getApplication(getActivity()).sendScreenName("Settings > Resto");
    }
}