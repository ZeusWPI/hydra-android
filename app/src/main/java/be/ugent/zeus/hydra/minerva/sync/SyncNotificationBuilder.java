package be.ugent.zeus.hydra.minerva.sync;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import be.ugent.zeus.hydra.R;

import static android.support.v4.app.NotificationCompat.CATEGORY_ERROR;

/**
 * Helper methods to display notifications relating to the Minerva sync.
 *
 * @author Niko Strijbol
 */
public class SyncNotificationBuilder {

    private static final int NOTIFICATION_ID = 600;

    /**
     * Show a notification prompting the user to renew their credentials.
     *
     * @param context A context.
     * @param intent The intent to launch a new activity.
     */
    public static void showError(Context context, Intent intent) {

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_notification_warning)
                .setAutoCancel(true)
                .setCategory(CATEGORY_ERROR)
                .setContentTitle("Minerva: opnieuw aanmelden")
                .setContentText("Druk om opnieuw aan te melden")
                .setContentIntent(pendingIntent)
                .setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle()
                        .bigText("Je moet opnieuw aanmelden bij Minerva, want de inloggegevens zijn verouderd.")
                );

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, builder.build());
    }

    /**
     * Show general error notification.
     *
     * @param context The context.
     */
    public static void showError(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_notification_warning)
                .setCategory(CATEGORY_ERROR)
                .setContentTitle("Minerva-fout")
                .setContentText("Synchronisatiefout")
                .setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle()
                        .bigText("Er trad een fout op bij het synchroniseren met Minerva. Gegevens kunnen verouderd zijn.")
                );

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, builder.build());
    }
}