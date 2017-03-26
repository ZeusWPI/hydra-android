package be.ugent.zeus.hydra.ui.preferences;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.service.notifications.NotificationScheduler;

/**
 * Preferences for the resto notification.
 *
 * @author Rien Maertens
 */
public class RestoPreferenceFragment extends PreferenceFragment {

    public static final String PREF_RESTO = "pref_resto_choice";
    public static final String PREF_DEFAULT_RESTO = "0";

    public static final String DEFAULT_CLOSING_TIME = "21:00";
    public static final String PREF_RESTO_CLOSING_HOUR = "pref_resto_closing_hour";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_resto);

        // Set and remove notifications
        final NotificationScheduler scheduler = new NotificationScheduler(getActivity());
        final CheckBoxPreference notificationCheckbox = (CheckBoxPreference) findPreference("pref_key_daily_notifications_checkbox");
        TimePreference notificationTime = (TimePreference) findPreference("pref_resto_notifications_time");

        notificationCheckbox.setOnPreferenceChangeListener((preference, newValue) -> {
            if((boolean)newValue){
                scheduler.scheduleNotification();
            } else {
                scheduler.cancelNotifications();
            }
            return true;
        });
        notificationTime.setOnPreferenceChangeListener((preference, newValue) -> {
            if(notificationCheckbox.isChecked()){
                scheduler.scheduleNotification((String) newValue);
            }
            return true;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        HydraApplication.getApplication(getActivity()).sendScreenName("Settings > Notifications");
    }
}

