package be.ugent.zeus.hydra.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;

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
            Intent tmpIntent = new Intent(context, Hydra.class);
            tmpIntent.setAction("RESTO");
            PendingIntent pIntent = PendingIntent.getActivity(
                    context,
                    0,
                    tmpIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
            Notification.Builder builder = new Notification.Builder(context)
                    .setContentTitle("Resto menu")
                    .setContentText("Check the restomenu in the hydra app.")
                    .setSmallIcon(R.drawable.logo) //TODO: Beter logo
                    .setContentIntent(pIntent)
                    .setAutoCancel(true);


            if (prefs.getBoolean("pref_key_daily_notifications_vibrate", false)){
                builder.setVibrate(new long[] {0, 300});
            }

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0, builder.build());
        } else {
            System.out.println(String.format(
                    Locale.getDefault(),
                    "Notification ignore: current day (%d) not in preferences: %s",
                    currentDay,
                    weekdays.toString()));
        }
    }
}
