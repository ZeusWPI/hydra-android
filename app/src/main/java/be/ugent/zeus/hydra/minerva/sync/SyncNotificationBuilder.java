package be.ugent.zeus.hydra.minerva.sync;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v7.app.NotificationCompat;

import be.ugent.zeus.hydra.R;

import static android.support.v4.app.NotificationCompat.CATEGORY_ERROR;

/**
 * @author Niko Strijbol
 */
public class SyncNotificationBuilder {

    private static final String TAG = "SyncNotificationBuilder";
    private static final int NOTIFICATION_ID = 600;


    public static void showError(Context context) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_error_24dp)
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
