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

package be.ugent.zeus.hydra.urgent;

import android.app.Notification;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import androidx.core.app.NotificationCompat;
import androidx.media.session.MediaButtonReceiver;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ChannelCreator;

/**
 * Manages the media notification(s).
 *
 * @author Niko Strijbol
 */
class MediaNotificationBuilder {

    private final Context context;

    MediaNotificationBuilder(Context context) {
        this.context = context;
        // Initialise the channel.
        ChannelCreator.createUrgentChannel(context);
    }

    Notification buildNotification(MediaSessionCompat mediaSession) {
        MediaControllerCompat controller = mediaSession.getController();
        var playbackState = controller.getPlaybackState();
        if (playbackState == null) {
            return null; // Nothing we can do currently.
        }

        // Construct the actual notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ChannelCreator.URGENT_CHANNEL);

        // Construct the style
        androidx.media.app.NotificationCompat.MediaStyle style =
                new androidx.media.app.NotificationCompat.MediaStyle(builder)
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP))
                        .setShowActionsInCompactView(0);

        // Construct the play/pause button.
        boolean isPlaying = playbackState.getState() == PlaybackStateCompat.STATE_PLAYING;
        boolean isConnecting = playbackState.getState() == PlaybackStateCompat.STATE_CONNECTING
                || playbackState.getState() == PlaybackStateCompat.STATE_BUFFERING;
        boolean isError = playbackState.getState() == PlaybackStateCompat.STATE_ERROR;
        if (isPlaying || isConnecting) {
            builder.addAction(new NotificationCompat.Action(
                    R.drawable.noti_ic_stop,
                    context.getString(R.string.urgent_stop),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_PAUSE))
            );
        } else if (isError) {
            builder.addAction(new NotificationCompat.Action(
                    R.drawable.noti_ic_stop,
                    context.getString(R.string.urgent_stop),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP))
            );
        } else {
            builder.addAction(
                    R.drawable.noti_ic_play_arrow_24dp,
                    context.getString(R.string.urgent_play),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_PLAY)
            );
        }

        builder.setSmallIcon(R.drawable.ic_notification_urgent)
                .setShowWhen(false)
                .setSubText(context.getString(R.string.urgent_fm))
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP))
                .setContentIntent(controller.getSessionActivity())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setChannelId(ChannelCreator.URGENT_CHANNEL)
                .setStyle(style);

        if (controller.getMetadata() != null) {
            MediaDescriptionCompat descriptionCompat = controller.getMetadata().getDescription();
            builder.setContentTitle(descriptionCompat.getTitle());
            if (isConnecting) {
                builder.setContentText(context.getString(R.string.urgent_loading));
            } else if (isError) {
                builder.setContentText(context.getString(R.string.urgent_error));
            } else {
                builder.setContentText(descriptionCompat.getSubtitle());
            }
            if (descriptionCompat.getIconBitmap() != null) {
                builder.setLargeIcon(descriptionCompat.getIconBitmap());
            } else {
                builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_album));
            }
        } else {
            builder.setContentTitle(context.getString(R.string.urgent_fm));
            if (isError) {
                builder.setContentText(context.getString(R.string.urgent_error));
            } else {
                builder.setContentText(context.getString(R.string.urgent_loading));
            }
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_album));
        }

        return builder.build();
    }
}
