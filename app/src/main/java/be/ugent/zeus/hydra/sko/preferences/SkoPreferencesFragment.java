package be.ugent.zeus.hydra.sko.preferences;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.notification.FirebaseMessageService;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Preferences for the SKO portion.
 *
 * @author Niko Strijbol
 */
public class SkoPreferencesFragment extends PreferenceFragment {

    public static final String PREF_SKO_NOTIFICATION = "pref_sko_notification";

    private boolean currentValue;
    private CheckBoxPreference preference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_sko);
        preference = (CheckBoxPreference) findPreference("pref_sko_notification");

        currentValue = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(PREF_SKO_NOTIFICATION, true);
    }

    @Override
    public void onPause() {
        super.onPause();

        if(preference.isChecked() != currentValue) {
            if(currentValue) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(FirebaseMessageService.SKO_TOPIC);
            } else {
                FirebaseMessaging.getInstance().subscribeToTopic(FirebaseMessageService.SKO_TOPIC);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        HydraApplication.getApplication(getActivity()).sendScreenName("Settings > SKO");
    }
}