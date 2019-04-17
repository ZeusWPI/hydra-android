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

    public static final String MINERVA_ANNOUNCEMENT_CHANNEL = "be.ugent.zeus.hydra.notifications.minerva.announcements";
    public static final String MINERVA_ACCOUNT_CHANNEL = "be.ugent.zeus.hydra.notifications.minerva";
    public static final String URGENT_CHANNEL = "be.ugent.zeus.hydra.notifications.urgent";

    private ChannelCreator() {
        throw new AssertionError("Utility class must not be instantiated.");
    }

    /**
     * Create a channel for the Minerva notifications.
     */
    @TargetApi(Build.VERSION_CODES.O)
    public static void createMinervaAnnouncementChannel(@NonNull Context context) {

        // Don't do anything on older versions.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        String channelName = context.getString(R.string.minerva_announcement_channel_title);
        String channelDescription = context.getString(R.string.minerva_announcement_channel_desc);

        NotificationChannel channel = new NotificationChannel(MINERVA_ANNOUNCEMENT_CHANNEL, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(channelDescription);
        channel.enableLights(true);
        getManager(context).createNotificationChannel(channel);
    }

    /**
     * Create a channel for maintenance and other app-specific notifications about Minerva.
     */
    @TargetApi(Build.VERSION_CODES.O)
    public static void createMinervaAccountChannel(@NonNull Context context) {

        // Don't do anything on older versions.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        String channelName = context.getString(R.string.minerva_account_channel_title);
        String channelDescription = context.getString(R.string.minerva_account_channel_desc);

        NotificationChannel channel = new NotificationChannel(MINERVA_ACCOUNT_CHANNEL, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(channelDescription);
        channel.enableLights(true);
        getManager(context).createNotificationChannel(channel);
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
