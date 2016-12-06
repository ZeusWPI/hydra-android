package be.ugent.zeus.hydra.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.NotificationCompat;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.MainActivity;

/**
 * @author Rien Maertens
 * @since 16/04/2016.
 */
public class NotificationCreator {

    public static final int NOTIFICATION_ID = 1;

    private Context context;
    private SharedPreferences preferences;


    public NotificationCreator(Context context, SharedPreferences preferences){
        this.context = context;
        this.preferences = preferences;
    }

    public Notification create() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MainActivity.ARG_TAB, 2);
        PendingIntent pending = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(context.getString(R.string.restonotification_title))
                .setContentText(context.getString(R.string.restonotification_text))
                .setSmallIcon(R.drawable.logo) //TODO: Beter logo
                .setContentIntent(pending)
                .setAutoCancel(true);

        if (preferences.getBoolean("pref_key_daily_notifications_vibrate", false)){
            builder.setVibrate(new long[] {0, 300});
        }

        // TODO: show a card instead of a small notification
        //RemoteViews menuCard = new RemoteViews(context.getPackageName(),R.layout.test_layout);
        //menuCard.se
        //menuCard.setImageViewResource(R.id.fbLogo, R.drawable.fblogolarge);
        //builder.setContent(menuCard);

        return builder.build();
    }

    public void createAndShow(){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, create());
    }
}
