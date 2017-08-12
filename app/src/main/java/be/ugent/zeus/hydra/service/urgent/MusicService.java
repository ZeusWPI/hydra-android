package be.ugent.zeus.hydra.service.urgent;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.main.MainActivity;

import java.util.Collections;
import java.util.List;

/**
 * TODO: handle noisy audio
 *
 * @author Niko Strijbol
 */
public class MusicService extends MediaBrowserServiceCompat implements MediaStateListener {

    private static final String TAG = "MusicService";
    private static final int MUSIC_SERVICE_ID = 1;
    private static final int REQUEST_CODE = 121;
    private MediaNotificationBuilder notificationBuilder;

    private MediaSessionCompat mediaSession;
    private Playback playback;
    private boolean isStarted = false;
    private PlaybackStateCompat.Builder stateCompatBuilder;
    private UrgentTrackProvider trackProvider;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: starting new service...");
        trackProvider = new UrgentTrackProvider(this);
        notificationBuilder = new MediaNotificationBuilder(this);

        playback = new Playback(this, this);

        // Start a MediaSession

        mediaSession = new MediaSessionCompat(this, TAG);
        setSessionToken(mediaSession.getSessionToken());
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mediaSession.setCallback(new SimpleSessionCallback(getApplicationContext(), playback, trackProvider, this::updateMetadata));

        stateCompatBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PAUSE
                        | PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_STOP
                        | PlaybackStateCompat.ACTION_PREPARE);
        mediaSession.setPlaybackState(stateCompatBuilder.build());

        Intent startThis = new Intent(this, MainActivity.class);
        startThis.putExtra(MainActivity.ARG_TAB, R.id.drawer_urgent);
        PendingIntent pi = PendingIntent.getActivity(this, REQUEST_CODE, startThis, PendingIntent.FLAG_UPDATE_CURRENT);
        mediaSession.setSessionActivity(pi);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaButtonReceiver.handleIntent(mediaSession, intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStateChanged(int oldState, int newState) {
        // TODO: should we move this somewhere else?
        switch (newState) {
            case MediaState.PREPARED:
                updateSessionState(PlaybackStateCompat.STATE_BUFFERING);
                break;
            case MediaState.ERROR:
                updateSessionState(PlaybackStateCompat.STATE_ERROR);
                break;
            case MediaState.PAUSED:
                updateSessionState(PlaybackStateCompat.STATE_PAUSED);
                break;
            case MediaState.PLAYBACK_COMPLETED:
                updateSessionState(PlaybackStateCompat.STATE_STOPPED);
                break;
            case MediaState.PREPARING:
                updateSessionState(PlaybackStateCompat.STATE_CONNECTING);
                break;
            case MediaState.STARTED:
                updateSessionState(PlaybackStateCompat.STATE_PLAYING);
                handlePlayRequest();
                break;
            case MediaState.STOPPED:
                updateSessionState(PlaybackStateCompat.STATE_STOPPED);
                stopSelf();
                break;
            case MediaState.END:
                stopSelf(); // Stop the service.
                updateSessionState(PlaybackStateCompat.STATE_NONE);
                break;
            case MediaState.IDLE:
            case MediaState.INITIALIZED:
            default:
                // Nothing.
        }

        updateNotification();
    }

    private void handlePlayRequest() {
        if (!isStarted) {
            startService(new Intent(getApplicationContext(), MusicService.class));
            isStarted = true;
        }
        if (!mediaSession.isActive()) {
            mediaSession.setActive(true);
        }

        MediaMetadataCompat data = trackProvider.getTrack();
        mediaSession.setMetadata(data);
    }

    private void updateSessionState(@PlaybackStateCompat.State int state) {
        // If there is no session yet, do nothing.
        if (mediaSession != null) {
            stateCompatBuilder.setState(state, 0, 1f);
            mediaSession.setPlaybackState(stateCompatBuilder.build());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        mediaSession.getController().getTransportControls().stop();
        mediaSession.release();
    }

    private void updateNotification() {
        Notification mediaNotification = notificationBuilder.buildNotification(mediaSession);

        if (playback.isPlaying()) {
            startForeground(MUSIC_SERVICE_ID, mediaNotification);
        } else {
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            //if (playback.getState() == MediaState.STOPPED || playback.getState() == MediaState.END) {
                manager.notify(MUSIC_SERVICE_ID, mediaNotification);
            //} else {
                //manager.cancel(MUSIC_SERVICE_ID);
            //}
            stopForeground(false);
        }
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        // If the client is us, allow browsing. Otherwise, don't allow any browsing.
        if (clientPackageName.equals(getPackageName())) {
            return new BrowserRoot(UrgentTrackProvider.MEDIA_ID_ROOT, null);
        } else {
            return new BrowserRoot(UrgentTrackProvider.MEDIA_ID_EMPTY_ROOT, null);
        }
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

        if (!trackProvider.hasTrackInformation()) {
            result.detach();
        }

        trackProvider.prepareMedia(success -> {
            if (success && parentId.equals(UrgentTrackProvider.MEDIA_ID_ROOT)) {

                // Set metadata
                mediaSession.setMetadata(trackProvider.getTrack());

                result.sendResult(Collections.singletonList(new MediaBrowserCompat.MediaItem(
                        trackProvider.getTrack().getDescription(),
                        MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
                )));
            } else {
                result.sendResult(Collections.emptyList());
            }
        });
    }

    public void updateMetadata() {
        mediaSession.setMetadata(trackProvider.getTrack());
    }
}