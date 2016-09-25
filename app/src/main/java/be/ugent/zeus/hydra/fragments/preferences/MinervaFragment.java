package be.ugent.zeus.hydra.fragments.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.minerva.auth.AccountUtils;
import be.ugent.zeus.hydra.minerva.sync.SyncUtils;

/**
 * Preferences for Minerva things.
 *
 * @author Niko Strijbol
 */
public class MinervaFragment extends PreferenceFragment {

    public static final String PREF_SYNC_FREQUENCY = "pref_minerva_sync_frequency";
    public static final String PREF_ANNOUNCEMENT_NOTIFICATION = "pref_minerva_announcement_notification";
    public static final String PREF_ANNOUNCEMENT_NOTIFICATION_EMAIL = "pref_minerva_announcement_notification_email";

    //In seconds
    public static final String PREF_DEFAULT_SYNC_FREQUENCY = "86400";
    public static final boolean PREF_DEFAULT_ANNOUNCEMENT_NOTIFICATION_EMAIL = false;

    private int oldSync;
    private int newSync;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_minerva);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getAppContext());
        oldSync = Integer.parseInt(preferences.getString(PREF_SYNC_FREQUENCY, PREF_DEFAULT_SYNC_FREQUENCY));
        newSync = oldSync;

        Preference preference = findPreference(PREF_SYNC_FREQUENCY);
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
        HydraApplication.getApplication(getActivity()).sendScreenName("Settings > Minerva");
    }

    private Context getAppContext() {
        return getActivity().getApplicationContext();
    }
}

