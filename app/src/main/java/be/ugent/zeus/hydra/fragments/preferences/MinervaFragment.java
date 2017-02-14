package be.ugent.zeus.hydra.fragments.preferences;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import android.provider.Settings;
import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.minerva.auth.AccountUtils;
import be.ugent.zeus.hydra.minerva.auth.MinervaConfig;
import be.ugent.zeus.hydra.minerva.sync.SyncUtils;

/**
 * Preferences for Minerva things.
 *
 * @author Niko Strijbol
 */
public class MinervaFragment extends PreferenceFragment {

    public static final String PREF_SYNC_FREQUENCY_COURSE = "pref_minerva_sync_course_frequency";
    public static final String PREF_SYNC_FREQUENCY_CALENDAR = "pref_minerva_sync_calendar_frequency";
    public static final String PREF_SYNC_FREQUENCY_ANNOUNCEMENT = "pref_minerva_sync_announcement_frequency";
    public static final String PREF_ANNOUNCEMENT_NOTIFICATION = "pref_minerva_announcement_notification";
    public static final String PREF_ANNOUNCEMENT_NOTIFICATION_EMAIL = "pref_minerva_announcement_notification_email";
    public static final String PREF_USE_MOBILE_URL = "pref_minerva_use_mobile_url";

    //In seconds
    public static final String PREF_DEFAULT_SYNC_FREQUENCY = "86400";
    public static final String PREF_DEFAULT_SYNC_LONG_FREQUENCY = "2592000";
    public static final boolean PREF_DEFAULT_ANNOUNCEMENT_NOTIFICATION_EMAIL = false;

    private int oldCourseSync;
    private int newCourseSync;
    private int oldAnnouncementSync;
    private int newAnnouncementSync;
    private int oldCalendarSync;
    private int newCalendarSync;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_minerva);

        // Open the account settings for the correct account.
        findPreference("account_settings").setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(Settings.ACTION_SYNC_SETTINGS);
            intent.putExtra(Settings.EXTRA_AUTHORITIES, new String[]{
                    MinervaConfig.COURSE_AUTHORITY,
                    MinervaConfig.ANNOUNCEMENT_AUTHORITY,
                    MinervaConfig.CALENDAR_AUTHORITY
            });
            getActivity().startActivity(intent);
            return true;
        });

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getAppContext());
        oldCourseSync = Integer.parseInt(preferences.getString(PREF_SYNC_FREQUENCY_COURSE, PREF_DEFAULT_SYNC_LONG_FREQUENCY));
        oldAnnouncementSync = Integer.parseInt(preferences.getString(PREF_SYNC_FREQUENCY_ANNOUNCEMENT, PREF_DEFAULT_SYNC_FREQUENCY));
        oldCalendarSync = Integer.parseInt(preferences.getString(PREF_SYNC_FREQUENCY_CALENDAR, PREF_DEFAULT_SYNC_FREQUENCY));
        newCourseSync = oldCourseSync;
        newAnnouncementSync = oldAnnouncementSync;
        newCalendarSync = oldCalendarSync;

        Preference coursePreference = findPreference(PREF_SYNC_FREQUENCY_COURSE);
        Preference announcementPreference = findPreference(PREF_SYNC_FREQUENCY_ANNOUNCEMENT);
        Preference calendarPreference = findPreference(PREF_SYNC_FREQUENCY_CALENDAR);

        coursePreference.setOnPreferenceChangeListener((preference, newValue) -> {
            newCourseSync = Integer.parseInt((String) newValue);
            return true;
        });

        announcementPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            newAnnouncementSync = Integer.parseInt((String) newValue);
            return true;
        });

        calendarPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            newCalendarSync = Integer.parseInt((String) newValue);
            return true;
        });

        if(!AccountUtils.hasAccount(getAppContext())) {
            coursePreference.setEnabled(false);
            announcementPreference.setEnabled(false);
            calendarPreference.setEnabled(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (oldCourseSync != newCourseSync) {
            SyncUtils.changeSyncFrequency(getAppContext(), MinervaConfig.COURSE_AUTHORITY, newCourseSync);
        }
        if (oldAnnouncementSync != newAnnouncementSync) {
            SyncUtils.changeSyncFrequency(getAppContext(), MinervaConfig.ANNOUNCEMENT_AUTHORITY, newAnnouncementSync);
        }
        if (oldCalendarSync != newCalendarSync) {
            SyncUtils.changeSyncFrequency(getAppContext(), MinervaConfig.CALENDAR_AUTHORITY, newCalendarSync);
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