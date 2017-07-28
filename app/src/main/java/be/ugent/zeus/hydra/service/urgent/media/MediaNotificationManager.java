package be.ugent.zeus.hydra.service.urgent.media;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.ChannelCreator;
import be.ugent.zeus.hydra.service.urgent.MusicService;
import be.ugent.zeus.hydra.service.urgent.track.Track;
import be.ugent.zeus.hydra.service.urgent.track.TrackManager;

/**
 * Manages the media notification(s).
 *
 * @author Niko Strijbol
 */
@Deprecated
public class MediaNotificationManager {

    private static final String TAG = "NotificationManager";
    private static final int NOTIFICATION_ID = 1;

    private final Context context;
    private final NotificationListener listener;

    @DrawableRes
    private int icon;
    private PendingIntent contentIntent;

    public MediaNotificationManager(Context context, NotificationListener listener) {
        this.context = context;
        this.listener = listener;
    }

    /**
     * Stop showing the current notification.
     * The service will stop being a foreground service as well.
     */
    public void remove() {
        this.listener.onCancelNotification(NOTIFICATION_ID);
    }

    private void publishNotification(NotificationCompat.Builder builder) {

        if (contentIntent != null) {
            builder.setContentIntent(contentIntent);
        } else {
            Log.e(TAG, "Did you forget to setContentIntent()? Nothing will happen when you touch the notification.");
        }

        if(icon == 0) {
            throw new IllegalStateException("You must set the notification icon before using it.");
        }

        listener.onPublishNotification(NOTIFICATION_ID, builder.build());
    }

    public void setIcon(@DrawableRes int icon) {
        this.icon = icon;
    }

    public void setContentIntent(PendingIntent intent) {
        this.contentIntent = intent;
    }

    public void show(MediaInfoProvider provider) {

        provider.updateMetadata();

        TrackManager trackManager = provider.getTrackManager();
        Track track;
        try {
            track = trackManager.currentTrack();
        } catch (IllegalStateException e) {
            return;
        }

        Intent intent = new Intent(context, MusicService.class);
        intent.setAction(MediaAction.FINISH);
        PendingIntent cancelIntent = PendingIntent.getService(context, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ChannelCreator.URGENT_CHANNEL);
        android.support.v4.media.app.NotificationCompat.MediaStyle style =
                new android.support.v4.media.app.NotificationCompat.MediaStyle(builder);
        style.setMediaSession(provider.getMediaToken());
        style.setShowCancelButton(true);
        style.setCancelButtonIntent(cancelIntent);

        //Position of the play pause button.
        int playPause = 0;

        if (trackManager.hasPrevious()) {
            builder.addAction(generateAction(R.drawable.noti_ic_skip_previous_24dp, "Previous", MediaAction.PREVIOUS)); //0
            playPause++;
        }

        if (provider.isPlaying()) {
            builder.addAction(generateAction(R.drawable.noti_ic_pause_24dp, "Pause", MediaAction.PAUSE)); //1
        } else {
            builder.addAction(generateAction(R.drawable.noti_ic_play_arrow_24dp, "Play", MediaAction.PLAY)); // 1
        }
        style.setShowActionsInCompactView(playPause);

        if (trackManager.hasNext()) {
            builder.addAction(generateAction(R.drawable.noti_ic_skip_next_24dp, "Next", MediaAction.NEXT)); //2
        }

        builder.setSmallIcon(icon)
                .setShowWhen(false)
                .setContentTitle(track.getTitle())
                .setContentText(track.getArtist())
                .setDeleteIntent(cancelIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setChannelId(ChannelCreator.URGENT_CHANNEL)
                .setStyle(style);

        //Add album artwork if available
        if (track.getAlbumArtwork() != null) {
            builder.setLargeIcon(track.getAlbumArtwork());
        } else {
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_album));
        }

        //We are ready! Show the notification.
        publishNotification(builder);
    }

    private NotificationCompat.Action generateAction(@DrawableRes int icon, String title, String intentAction) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction(intentAction);
        PendingIntent pendingIntent = PendingIntent.getService(context, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Action.Builder(icon, title, pendingIntent).build();
    }

    public interface NotificationListener {

        void onCancelNotification(int id);

        void onPublishNotification(int id, Notification notification);
    }

    public interface MediaInfoProvider {

        boolean isPlaying();

        MediaSessionCompat.Token getMediaToken();

        TrackManager getTrackManager();

        void updateMetadata();
    }
}