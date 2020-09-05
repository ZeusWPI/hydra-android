package be.ugent.zeus.hydra.urgent;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import java.util.Collections;
import java.util.List;

import be.ugent.zeus.hydra.MainActivity;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.reporting.Event;
import be.ugent.zeus.hydra.common.reporting.Reporting;
import be.ugent.zeus.hydra.urgent.player.Player;
import be.ugent.zeus.hydra.urgent.player.PlayerSessionServiceCallback;
import be.ugent.zeus.hydra.urgent.player.SessionPlayerServiceCallback;
import be.ugent.zeus.hydra.urgent.player.UrgentTrackProvider;

/**
 * Service for streaming audio from Urgent.fm.
 * <br>
 * It is highly recommended to read the Android documentation first, before working on these classes. For example,
 * the term media session is strictly used to denote the media session managed by Android itself, through the
 * {@link MediaSessionCompat} class.
 *
 * <h2>Service</h2>
 * The service is responsible for keeping the stream alive and managing the various background permissions and
 * restrictions imposed by Android. It is not responsible for controlling the audio.
 *
 * <h2>Audio controls</h2>
 * The main responsibility of this service is keeping an instance of {@link Player} alive. The service will start the
 * player, construct a media session and connect everything up. Afterwards, control is given up to the media session.
 *
 * @author Niko Strijbol
 * @see <a href="https://developer.android.com/guide/topics/media/mediaplayer">Offical documentation</a>
 * @see Player
 */
public class MusicService extends MediaBrowserServiceCompat implements
        SessionPlayerServiceCallback,
        PlayerSessionServiceCallback {

    // We do not support browsing media.
    private static final String MEDIA_ID_ROOT = "__ROOT__";
    // We do not support browsing media.
    private static final String MEDIA_ID_EMPTY_ROOT = "__EMPTY__";

    private static final String TAG = "MusicService";
    private static final String WIFI_LOCK_TAG = "UrgentMusic";
    private static final int MUSIC_SERVICE_ID = 1;
    private static final int REQUEST_CODE = 121;

    /**
     * The notification builder.
     */
    private MediaNotificationBuilder notificationBuilder;

    /**
     * The player used to play the media.
     */
    private Player player;

    /**
     * The media session.
     */
    private MediaSessionCompat mediaSession;

    private NotificationManagerCompat notificationManager;

    private WifiManager.WifiLock wifiLock;

    private boolean foreground = false;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate: starting new service...");
        notificationManager = NotificationManagerCompat.from(this);

        // Create the WiFi lock we we will use later.
        WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (manager != null) {
            //noinspection deprecation
            this.wifiLock = manager.createWifiLock(WifiManager.WIFI_MODE_FULL, WIFI_LOCK_TAG);
        }

        // Create the media session.
        mediaSession = new MediaSessionCompat(this, TAG);
        setSessionToken(mediaSession.getSessionToken());

        // Create the player.
        player = new Player.Builder(this)
                .withSession(mediaSession)
                .withCallback1(this)
                .withCallback2(this)
                .build();

        // Create the notification builder.
        notificationBuilder = new MediaNotificationBuilder(this);

        // Add the activity intent to the session.
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
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Destroying Music Service...");
        mediaSession.release();
        player.destroy();
        if (wifiLock != null && wifiLock.isHeld()) {
            wifiLock.release(); // To be sure.
        }
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        // If the client is us, allow browsing. Otherwise, don't allow any browsing.
        if (clientPackageName.equals(getPackageName())) {
            return new BrowserRoot(MEDIA_ID_ROOT, null);
        } else {
            return new BrowserRoot(MEDIA_ID_EMPTY_ROOT, null);
        }
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

        UrgentTrackProvider trackProvider = player.getProvider();

        // If there is not track information, detach.
        if (!trackProvider.hasTrackInformation()) {
            result.detach();
        }

        trackProvider.prepareMedia(data -> {
            if (data != null && parentId.equals(MEDIA_ID_ROOT)) {
                mediaSession.setMetadata(data);
                result.sendResult(Collections.singletonList(new MediaBrowserCompat.MediaItem(
                        data.getDescription(),
                        MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
                )));
            } else {
                result.sendResult(Collections.emptyList());
            }
        });
    }

    private Notification constructNotification() {
        // If required objects are null, return null.
        if (mediaSession == null || mediaSession.getController() == null) {
            return null;
        }

        return notificationBuilder.buildNotification(mediaSession);
    }

    @Override
    public void onSessionStateChanged(int newState) {
        Log.d(TAG, "onSessionStateChanged: new state is " + newState);
        updateNotification();
    }

    @Override
    public void onMetadataUpdate() {
        updateNotification();
    }

    private void updateNotification() {
        Notification notification = constructNotification();
        if (notification != null) {
            if (foreground) {
                notificationManager.notify(MUSIC_SERVICE_ID, notification);
            } else {
                Log.w(TAG, "Ignored notification update, as there is no notification.");
            }
        }
    }

    @Override
    public void onPlay() {
        Notification notification = constructNotification();
        Log.d(TAG, "onPlay: notification is " + notification);
        if (notification == null) {
            stopSelf();
        } else {
            if (wifiLock != null) {
                wifiLock.acquire();
            }
            ContextCompat.startForegroundService(getApplicationContext(), new Intent(getApplicationContext(), MusicService.class));
            mediaSession.setActive(true);
            Log.d(TAG, "onPlay: starting foreground service");
            startForeground(MUSIC_SERVICE_ID, notification);
            Reporting.getTracker(this).log(new MusicStartEvent());
            foreground = true;
        }
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause called");
        if (wifiLock != null && wifiLock.isHeld()) {
            wifiLock.release();
        }
        stopForeground(false);
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop called");
        if (wifiLock != null && wifiLock.isHeld()) {
            wifiLock.release();
        }
        Reporting.getTracker(this).log(new MusicStopEvent());
        stopForeground(true);
        foreground = false;
    }

    private static class MusicStartEvent implements Event {
        @Nullable
        @Override
        public String getEventName() {
            return "be.ugent.zeus.hydra.urgent.analytics.music_start";
        }
    }

    private static class MusicStopEvent implements Event {
        @Nullable
        @Override
        public String getEventName() {
            return "be.ugent.zeus.hydra.urgent.analytics.music_stop";
        }
    }
}
