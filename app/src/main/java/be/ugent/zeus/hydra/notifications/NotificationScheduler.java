package be.ugent.zeus.hydra.notifications;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZonedDateTime;

/**
 * @author Rien Maertens
 * @since 16/04/2016.
 */
public class NotificationScheduler {
    private static final int REQUEST_CODE = 0;
    private static final int FLAGS = 0;

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
        scheduleNotification(preferences.getString("pref_resto_notifications_time", "12:00"));
    }

    public void scheduleNotification(String time) {

        LocalTime notificationTime = LocalTime.parse(time);
        LocalDateTime now = LocalDateTime.now();

        ZonedDateTime nextOccurrence = ZonedDateTime.now();

        //Get the next occurrence
        if(notificationTime.isAfter(now.toLocalTime())) {
            nextOccurrence = nextOccurrence.plusDays(1);
        }

        nextOccurrence = nextOccurrence.withHour(notificationTime.getHour()).withMinute(notificationTime.getMinute());

        cancelNotifications();

        // Daily alarm
        alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                nextOccurrence.toInstant().toEpochMilli(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent);
    }
}