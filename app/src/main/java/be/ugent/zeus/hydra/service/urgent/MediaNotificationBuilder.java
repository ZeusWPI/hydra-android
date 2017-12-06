package be.ugent.zeus.hydra.service.urgent;

import android.app.Notification;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.ChannelCreator;

/**
 * Manages the media notification(s).
 *
 * @author Niko Strijbol
 */
public class MediaNotificationBuilder {

    private final Context context;

    public MediaNotificationBuilder(Context context) {
        this.context = context;
    }

    public Notification buildNotification(MediaSessionCompat mediaSession) {
        MediaControllerCompat controller = mediaSession.getController();

        // Construct the actual notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ChannelCreator.URGENT_CHANNEL);

        // Construct the style
        android.support.v4.media.app.NotificationCompat.MediaStyle style =
                new android.support.v4.media.app.NotificationCompat.MediaStyle(builder)
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP))
                        .setShowActionsInCompactView(0);

        // Construct the play/pause button.
        boolean isPlaying = controller.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING;
        if (isPlaying) {
            builder.addAction(new NotificationCompat.Action(
                    R.drawable.noti_ic_stop,
                    context.getString(R.string.urgent_stop),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_PAUSE))
            );
        } else {
            builder.addAction(
                    R.drawable.noti_ic_play_arrow_24dp,
                    context.getString(R.string.urgent_play),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_PLAY)
            );
        }

        MediaDescriptionCompat descriptionCompat = controller.getMetadata().getDescription();

        builder.setSmallIcon(R.drawable.ic_notification_urgent)
                .setShowWhen(false)
               // .setColor(ContextCompat.getColor(context, R.color.ugent_blue_dark))
                .setContentTitle(descriptionCompat.getTitle())
                .setContentText(descriptionCompat.getSubtitle())
                .setSubText(context.getString(R.string.urgent_fm))
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP))
                .setContentIntent(controller.getSessionActivity())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setChannelId(ChannelCreator.URGENT_CHANNEL)
                .setStyle(style);


        //Add album artwork if available
        if (descriptionCompat.getIconBitmap() != null) {
            builder.setLargeIcon(descriptionCompat.getIconBitmap());
        } else {
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_album));
        }

        return builder.build();
    }
}