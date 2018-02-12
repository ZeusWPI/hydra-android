package be.ugent.zeus.hydra.theme;

import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;
import android.widget.Toast;

import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;

/**
 * Show preferences related to the news-section.
 *
 * @author Niko Strijbol
 */
public class ThemePreferenceFragment extends PreferenceFragment {

    public static final String PREF_THEME_NIGHT_MODE = "pref_theme_night_mode";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_theme);
        ListPreference listPreference = (ListPreference) findPreference(PREF_THEME_NIGHT_MODE);
        listPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            Toast.makeText(getActivity().getApplicationContext(), R.string.pref_theme_night_mode_restart, Toast.LENGTH_LONG).show();
            return true;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        HydraApplication.getApplication(getActivity()).sendScreenName("Settings > Theme");
    }

    @AppCompatDelegate.NightMode
    public static int getNightMode(Context context) {
        String value = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_THEME_NIGHT_MODE, "3");
        switch (value) {
            case "0":
                return AppCompatDelegate.MODE_NIGHT_AUTO;
            case "1":
                return AppCompatDelegate.MODE_NIGHT_NO;
            case "2":
                return AppCompatDelegate.MODE_NIGHT_YES;
            case "3":
                return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }
        return -1;
    }
}