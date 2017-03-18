package be.ugent.zeus.hydra.service.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * @author Rien Maertens
 * @since 07/04/2016.
 */
public class DailyNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> weekdays =
                prefs.getStringSet("pref_daily_notifications_days_of_week", new HashSet<>());


        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        boolean notificationToday = weekdays.contains(String.valueOf(currentDay));
        notificationToday |= prefs.getBoolean("pref_daily_notifications_always", false);

        if (notificationToday) {
            new NotificationCreator(context, prefs).createAndShow();
        } else {
            System.out.println(String.format(
                    Locale.getDefault(),
                    "Notification ignore: current day (%d) not in preferences: %s",
                    currentDay,
                    weekdays.toString()));
        }
    }
}
