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
        ChannelCreator.getInstance(context).createUrgentChannel();
    }

    Notification buildNotification(MediaSessionCompat mediaSession) {
        MediaControllerCompat controller = mediaSession.getController();

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
        boolean isPlaying = controller.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING;
        boolean isConnecting = controller.getPlaybackState().getState() == PlaybackStateCompat.STATE_CONNECTING
                || controller.getPlaybackState().getState() == PlaybackStateCompat.STATE_BUFFERING;
        boolean isError = controller.getPlaybackState().getState() == PlaybackStateCompat.STATE_ERROR;
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
