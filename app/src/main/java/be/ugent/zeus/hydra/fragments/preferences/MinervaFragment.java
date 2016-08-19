package be.ugent.zeus.hydra.fragments.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.auth.AccountUtils;
import be.ugent.zeus.hydra.minerva.sync.SyncUtils;

/**
 * @author Niko Strijbol
 */
public class MinervaFragment extends PreferenceFragment {

    private int oldSync;
    private int newSync;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences_minerva);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getAppContext());
        oldSync = Integer.parseInt(preferences.getString("pref_minerva_sync_frequency", "86400"));
        newSync = oldSync;

        Preference preference = findPreference("pref_minerva_sync_frequency");
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                newSync = Integer.parseInt((String) o);
                return true;
            }
        });

        if(!AccountUtils.hasAccount(getAppContext())) {
            preference.setEnabled(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(oldSync != newSync) {
            SyncUtils.changeSyncFrequency(getAppContext(), newSync);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        HydraApplication happ = (HydraApplication) getActivity().getApplication();
        happ.sendScreenName("settings");
    }

    private Context getAppContext() {
        return getActivity().getApplicationContext();
    }
}

