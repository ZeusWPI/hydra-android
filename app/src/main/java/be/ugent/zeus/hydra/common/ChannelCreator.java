/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
 * @author Niko Strijbol
 */
public class ChannelCreator {

    public static final String URGENT_CHANNEL = "be.ugent.zeus.hydra.notifications.urgent";
    public static final String WPI_WIDGET_CHANNEL = "be.ugent.zeus.hydra.notifications.wpi.door_widget";

    private ChannelCreator() {
        throw new AssertionError("Utility class must not be instantiated.");
    }

    /**
     * Create a channel for the Urgent.fm player.
     */
    @TargetApi(26)
    public static void createUrgentChannel(@NonNull Context context) {
        // Don't do anything on older versions.
        if (Build.VERSION.SDK_INT < 26) {
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

    /**
     * Create a channel for the notification if opening the door takes too long
     */
    @TargetApi(26)
    public static void createWpiWidgetChannel(@NonNull Context context) {
        // Don't do anything on older versions.
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }

        String channelName = context.getString(R.string.wpi_door_channel_title);
        String channelDescription = context.getString(R.string.wpi_door_channel_description);

        NotificationChannel channel = new NotificationChannel(WPI_WIDGET_CHANNEL, channelName, NotificationManager.IMPORTANCE_LOW);
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
