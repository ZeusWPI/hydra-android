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
public class SyncErrorNotification {

    private static final int NOTIFICATION_ID = 600;

    private final NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    private Context context;

    private SyncErrorNotification(Context context) {
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.builder = new NotificationCompat.Builder(context);
        this.context = context;
    }

    /**
     * Show the notification.
     */
    public void show() {
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    /**
     * Hide the notification.
     */
    public void remove() {
        notificationManager.cancel(NOTIFICATION_ID);
    }

    public static class Builder {

        private SyncErrorNotification notification;

        public Builder(Context context) {
            notification = new SyncErrorNotification(context);
            notification.builder.setSmallIcon(R.drawable.ic_notification_warning)
            .setCategory(CATEGORY_ERROR);
        }

        /**
         * Same as the constructor, but enables better chaining of methods.
         *
         * @see #Builder(Context)
         */
        public static Builder init(Context context) {
            return new Builder(context);
        }

        /**
         * Show a notification for an auth error that requires the user to enter their credentials.
         *
         * @param authIntent The intent produced by the authentication manager.
         *
         * @return This builder.
         */
        public Builder authError(Intent authIntent) {
            PendingIntent pendingIntent = PendingIntent.getActivity(notification.context, 0, authIntent, 0);
            notification.builder.setAutoCancel(true)
                    .setContentTitle("Minerva: opnieuw aanmelden")
                    .setContentText("Druk om opnieuw aan te melden")
                    .setContentIntent(pendingIntent)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("Je moet opnieuw aanmelden bij Minerva, want de inloggegevens zijn verouderd.")
                    );
            return this;
        }

        /**
         * Show a generic error.
         *
         * @return This builder.
         */
        public Builder genericError() {
            notification.builder.setContentTitle("Minerva-fout")
                    .setContentText("Synchronisatiefout")
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("Er trad een fout op bij het synchroniseren met Minerva. Gegevens kunnen verouderd zijn.")
                    );
            return this;
        }

        /**
         * @return The notification.
         */
        public SyncErrorNotification build() {
            return notification;
        }
    }
}