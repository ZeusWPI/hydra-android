package be.ugent.zeus.hydra.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
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
        boolean notificationToday = true;
        //TODO: let the user pick on which day he wants a notification
        if (notificationToday) {
            Intent tmpIntent = new Intent(context, Hydra.class);
            // TODO: Open resto
            PendingIntent pIntent = PendingIntent.getActivity(context, 0, tmpIntent, 0);

            Notification.Builder builder = new Notification.Builder(context)
                    .setContentTitle("Resto menu")
                    .setContentText("Check the restomenu in the hydra app.")
                    .setSmallIcon(R.drawable.logo) //TODO: Beter logo
                    .setContentIntent(pIntent)
                    .setAutoCancel(true);


            if (PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean("pref_key_daily_notifications_vibrate", false)){
                builder.setVibrate(new long[] {0, 300});
            }

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0, builder.build());
        }
    }
}
