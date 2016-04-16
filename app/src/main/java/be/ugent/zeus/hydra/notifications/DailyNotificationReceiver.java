package be.ugent.zeus.hydra.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.Hydra;

/**
 * @author Rien Maertens
 * @since 07/04/2016.
 */
public class DailyNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> weekdays =
                prefs.getStringSet("pref_daily_notifications_days_of_week", new HashSet<String>());


        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        boolean notificationToday = weekdays.contains(String.valueOf(currentDay));
        notificationToday |= prefs.getBoolean("pref_daily_notifications_always", false);

        if (notificationToday) {
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCreator creator = new NotificationCreator(context, prefs);
            Notification restoNotification = creator.create();
            notificationManager.notify(0, restoNotification);
        } else {
            System.out.println(String.format(
                    Locale.getDefault(),
                    "Notification ignore: current day (%d) not in preferences: %s",
                    currentDay,
                    weekdays.toString()));
        }
    }
}
