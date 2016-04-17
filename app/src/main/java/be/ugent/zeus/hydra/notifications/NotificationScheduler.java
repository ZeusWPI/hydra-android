package be.ugent.zeus.hydra.notifications;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;

import be.ugent.zeus.hydra.preference.Time;

/**
 * @author Rien Maertens
 * @since 16/04/2016.
 */
public class NotificationScheduler {
    private static int REQUEST_CODE = 0;
    private static int FLAGS = 0;

    private AlarmManager alarmManager;
    private Activity activity;
    private PendingIntent pendingIntent;
    private SharedPreferences preferences;

    public NotificationScheduler(Activity activity){
        this.activity = activity;
        Intent intent = new Intent(activity, DailyNotificationReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(activity, REQUEST_CODE, intent, FLAGS);
        alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        preferences = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    public void cancelNotifications(){
        alarmManager.cancel(pendingIntent);
    }

    public void testNotification(){
        new NotificationCreator(activity.getApplicationContext(), preferences).createAndShow();
    }

    public void scheduleNotification(){
        scheduleNotification(preferences.getInt("pref_daily_notifications_time", 0));
    }

    public void scheduleNotification(Object time){
        Time timeObj = new Time(time);
        Calendar cal = timeObj.nextOccurrence();
        cancelNotifications();

        // Daily alarm
        alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                cal.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent);
    }
}
