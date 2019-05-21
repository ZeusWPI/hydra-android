package be.ugent.zeus.hydra.common;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.Objects;

import be.ugent.zeus.hydra.R;

/**
 * Creates the channels for the notifications.
 *
 * TODO: should these methods be moved to where they are actually used?
 *
 * @author Niko Strijbol
 */
public class ChannelCreator {

    public static final String URGENT_CHANNEL = "be.ugent.zeus.hydra.notifications.urgent";

    private ChannelCreator() {
        throw new AssertionError("Utility class must not be instantiated.");
    }

    /**
     * Create a channel for the Urgent.fm player.
     */
    @TargetApi(Build.VERSION_CODES.O)
    public static void createUrgentChannel(@NonNull Context context) {
        // Don't do anything on older versions.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        String channelName = context.getString(R.string.urgent_channel_title);
        String channelDescription = context.getString(R.string.urgent_channel_desc);

        NotificationChannel channel = new NotificationChannel(URGENT_CHANNEL, channelName, NotificationManager.IMPORTANCE_LOW);
        channel.setDescription(channelDescription);
        channel.enableLights(false);
        channel.enableVibration(false);
        getManager(context).createNotificationChannel(channel);
    }

    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.M)
    private static NotificationManager getManager(@NonNull Context context) {
        return Objects.requireNonNull(context.getSystemService(NotificationManager.class));
    }
}
