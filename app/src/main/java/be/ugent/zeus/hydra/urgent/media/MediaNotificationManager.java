package be.ugent.zeus.hydra.urgent.media;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.urgent.MusicService;
import be.ugent.zeus.hydra.urgent.track.Track;
import be.ugent.zeus.hydra.urgent.track.TrackManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashSet;
import java.util.Set;

/**
 * Manages the media notification(s).
 *
 * @author Niko Strijbol
 */
public class MediaNotificationManager {

    private static final String TAG = "NotificationManager";
    private static final int NOTIFICATION_ID = 1;

    private final Context context;
    private final NotificationListener listener;

    //TODO check if this needs a better hashcode
    private Set<Target> targets = new HashSet<>();

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

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        NotificationCompat.MediaStyle style = new NotificationCompat.MediaStyle(builder);
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
                .setStyle(style);

        if (!TextUtils.isEmpty(track.getArtworkUrl())) {
            try {
                Target artTarget = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        builder.setLargeIcon(bitmap);
                        publishNotification(builder);
                        targets.remove(this);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        publishNotification(builder);
                        targets.remove(this);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                targets.add(artTarget);
                Picasso.with(context)
                        .load(track.getArtworkUrl())
                        .into(artTarget);
            } catch (IllegalArgumentException e) {
                Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.id.albumImage);
                builder.setLargeIcon(icon);
                //no artwork. Ignore.
                publishNotification(builder);
            }
        }else{
            Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.id.albumImage);
            builder.setLargeIcon(icon);
            publishNotification(builder);
        }
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