package be.ugent.zeus.hydra.fragments.settings;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.notifications.NotificationScheduler;
import be.ugent.zeus.hydra.preference.TimePreference;

/**
 * @author Rien Maertens
 * @since 16/02/2016.
 *
 *
SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
boolean sf = sharedPrefs.getBoolean("pref_association_checkbox", false);
 */
public class NotificationFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.notifications);

        // Set and remove notifications
        final NotificationScheduler scheduler = new NotificationScheduler(getActivity());
        final CheckBoxPreference notificationCheckbox =
                (CheckBoxPreference) findPreference("pref_key_daily_notifications_checkbox");
        TimePreference notificationTime =
                (TimePreference) findPreference("pref_daily_notifications_time") ;

        notificationCheckbox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if((boolean)newValue){
                    scheduler.scheduleNotification();
                } else {
                    scheduler.cancelNotifications();
                }
                return true;
            }
        });
        notificationTime.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if(notificationCheckbox.isChecked()){
                    scheduler.scheduleNotification(newValue);
                }
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        HydraApplication happ = (HydraApplication) getActivity().getApplication();
        happ.sendScreenName("settings");
    }
}

