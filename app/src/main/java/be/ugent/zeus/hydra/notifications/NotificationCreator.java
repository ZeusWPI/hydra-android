package be.ugent.zeus.hydra.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.Hydra;

/**
 * @author Rien Maertens
 * @since 16/04/2016.
 */
public class NotificationCreator {

    private Context context;
    private SharedPreferences preferences;


    public NotificationCreator(Context context, SharedPreferences preferences){
        this.context = context;
        this.preferences = preferences;
    }

    public Notification create(){
        Intent tmpIntent = new Intent(context, Hydra.class);
        tmpIntent.setAction(context.getString(R.string.resto_action));
        PendingIntent pIntent = PendingIntent.getActivity(
                context,
                0,
                tmpIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(context.getString(R.string.restonotification_title))
                .setContentText(context.getString(R.string.restonotification_text))
                .setSmallIcon(R.drawable.logo) //TODO: Beter logo
                .setContentIntent(pIntent)
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
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, create());
    }
}
