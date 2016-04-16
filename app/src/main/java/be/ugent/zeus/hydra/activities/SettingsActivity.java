package be.ugent.zeus.hydra.activities;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationCompat;
import android.view.View;

import java.util.Calendar;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.fragments.SettingsFragment;
import be.ugent.zeus.hydra.receiver.DailyNotificationReceiver;

/**
 * @author Rien Maertens
 * @since 16/02/2016.
 */
public class SettingsActivity extends AppCompatActivity {

    SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.HydraActionBar);
        super.onCreate(savedInstanceState);
        // Display the fragment as the main content.

        settingsFragment = new SettingsFragment();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, settingsFragment).commit();



    }

    public void testNotification(View view){
        settingsFragment.testNotification();
    }
}
