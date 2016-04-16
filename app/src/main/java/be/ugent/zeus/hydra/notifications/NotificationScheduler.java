package be.ugent.zeus.hydra.notifications;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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
        scheduleNotification(preferences.getString("pref_daily_notifications_time", null));
    }

    public void scheduleNotification(String time){
        String hour_minute[] = time.split(":");
        Calendar cal = Calendar.getInstance();
        int hour = Integer.parseInt(hour_minute[0], 10);
        int minute = Integer.parseInt(hour_minute[1], 10);
        if(!(hour <= cal.get(Calendar.HOUR_OF_DAY) && cal.get(Calendar.MINUTE) < minute)){
            cal.add(Calendar.DATE, 1);
        }
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cancelNotifications();

        // Daily alarm
        alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                cal.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent);
    }
}
