package be.ugent.zeus.hydra.service.urgent.media;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.ChannelCreator;
import be.ugent.zeus.hydra.service.urgent.MusicService;
import be.ugent.zeus.hydra.service.urgent.track.Track;
import be.ugent.zeus.hydra.ui.main.MainActivity;

/**
 * Manages the media notification(s).
 *
 * @author Niko Strijbol
 */
public class MediaNotificationBuilder {

    private static final String TAG = "NotificationManager";
    private static final int NOTIFICATION_ID = 1;
    private static final int NOTIFICATION_ID_PREPARING = 2;

    private final Context context;

    public MediaNotificationBuilder(Context context) {
        this.context = context;
    }

    public Notification buildNotification(MediaInfoProvider provider) {

        Track track = provider.getTrack();

        // Get the click intent
        PendingIntent clickIntent = buildClickIntent();

        // Construct the actual notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ChannelCreator.URGENT_CHANNEL);

        // Construct the style
        android.support.v4.media.app.NotificationCompat.MediaStyle style =
                new android.support.v4.media.app.NotificationCompat.MediaStyle(builder)
                .setMediaSession(provider.getMediaToken())
                .setShowCancelButton(true)
                .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP))
                .setShowActionsInCompactView(0);

        // Construct the play/pause button.
        if (provider.isPlaying()) {
            builder.addAction(new NotificationCompat.Action(
                    R.drawable.noti_ic_pause_24dp,
                    context.getString(R.string.urgent_pause),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_PAUSE))
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
                .setContentTitle(track.getTitle())
                .setContentText(track.getArtist())
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP))
                .setContentIntent(clickIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setChannelId(ChannelCreator.URGENT_CHANNEL)
                .setStyle(style);

        //Add album artwork if available
        if (track.getAlbumArtwork() != null) {
            builder.setLargeIcon(track.getAlbumArtwork());
        } else {
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_album));
        }

        return builder.build();
    }

    private NotificationCompat.Action generateAction(@DrawableRes int icon, @StringRes int title, String intentAction) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction(intentAction);
        PendingIntent pendingIntent = PendingIntent.getService(context, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        String titleString = context.getString(title);
        return new NotificationCompat.Action.Builder(icon, titleString, pendingIntent).build();
    }

    public interface MediaInfoProvider {

        boolean isPlaying();

        MediaSessionCompat.Token getMediaToken();

        Track getTrack();
    }

    private PendingIntent buildClickIntent() {
        Intent startThis = new Intent(context, MainActivity.class);
        startThis.putExtra(MainActivity.ARG_TAB, R.id.drawer_urgent);
        return PendingIntent.getActivity(context, 0, startThis, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public Notification buildPreparingNotification() {

        // Get the click intent
        PendingIntent clickIntent = buildClickIntent();

        return new NotificationCompat.Builder(context, ChannelCreator.URGENT_CHANNEL)
                .setSmallIcon(R.drawable.ic_notification_urgent)
                .setShowWhen(false)
                .setContentTitle("TEST PREPARING")
                .setContentIntent(clickIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setChannelId(ChannelCreator.URGENT_CHANNEL)
                //.setLargeIcon(BitmapFactory.decodeResource(context.getResources())VectorDrawableCompat.create(context.getResources(), R.drawable.ic_urgent_notification, null).)
                .build();
    }
}