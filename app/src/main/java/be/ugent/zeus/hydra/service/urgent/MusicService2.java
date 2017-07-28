package be.ugent.zeus.hydra.service.urgent;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import be.ugent.zeus.hydra.data.models.UrgentTrack;
import be.ugent.zeus.hydra.service.urgent.media.MediaNotificationBuilder;
import be.ugent.zeus.hydra.service.urgent.media.SimpleSessionCallback2;
import be.ugent.zeus.hydra.service.urgent.track.Track;
import java8.util.Objects;
import java8.util.function.Consumer;

import java.io.IOException;

/**
 * TODO: handle noisy audio
 * TODO: look at mediabuttoneventreceiver
 * @author Niko Strijbol
 */
public class MusicService2 extends Service implements MediaStateListener, AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = "MusicService2";
    private static final int MUSIC_SERVICE_ID = 1;
    private static final String WIFI_LOCK_TAG = "UrgentMusic";

    public static final String ARG_START_PLAYING = "arg_start_playing";

    public static final int REQUEST_PERMISSION_WAKE_LOCK = 1;

    private final IBinder binder = new MusicBinder2(this);

    private MediaNotificationBuilder notificationBuilder;
    private Track track;

    private MediaSessionCompat mediaSession;
    private WifiManager.WifiLock wifiLock;
    private MediaManager mediaManager;
    private boolean initialized = false;
    private Consumer<MediaSessionCompat.Token> tokenConsumer;
    private boolean isMediaSessionPrepared = false;
    private boolean isPrepared = false;
    private PlaybackStateCompat.Builder stateCompatBuilder;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationBuilder = new MediaNotificationBuilder(getApplicationContext());
        track = new UrgentTrack(getApplicationContext());
        Log.d(TAG, "onCreate: starting new service...");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand: Received intent");

        if (!initialized) {
            Log.d(TAG, "onStartCommand: Doing set up");
            setUp();
            initialized = true;
        }

        MediaButtonReceiver.handleIntent(mediaSession, intent);

        return START_STICKY;
    }

    public void initPlay() {
        if (mediaSession != null) {
            mediaSession.getController().getTransportControls().play();
        }
    }

    private void setUp() {

        // Set up the media player.
        initMediaPlayer();

        // Get the wake lock instance.
        wifiLock = ((WifiManager) getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, WIFI_LOCK_TAG);

        // Set up the media session.
        initMediaSession();
    }

    /**
     * Initialises the media player and starts retrieving the URL to play.
     */
    private void initMediaPlayer() {

        Log.d(TAG, "initMediaPlayer: initing media player");

        mediaManager = new MediaManager(getApplicationContext(), this);

        // Start getting the URL.

        track.getUrl(s -> {
            try {
                mediaManager.prepare(s);
            } catch (IOException e) {
                Log.e(TAG, "Could not get Urgent URL, stopping the service.", e);
                stopSelf();
            }
        });
    }

    /**
     * Initialises the media session. MUST be called after the media player was set up.
     */
    private void initMediaSession() {

        Log.d(TAG, "initMediaSession: initing media session");

        //TODO
        Objects.requireNonNull(mediaManager);

        // TODO: sort out the media buttons handlers
        mediaSession = new MediaSessionCompat(getApplicationContext(), TAG);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setCallback(new SimpleSessionCallback2(mediaManager));
        mediaSession.setActive(true);
        stateCompatBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PAUSE
                        | PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_STOP
                        | PlaybackStateCompat.ACTION_PREPARE);
        mediaSession.setPlaybackState(stateCompatBuilder.build());

        // Set metadata
        final MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        builder.putText(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, track.getArtist());
        builder.putText(MediaMetadataCompat.METADATA_KEY_TITLE, track.getTitle());

        //Add the album artwork if it is available.
        if (track.getAlbumArtwork() != null) {
            builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, track.getAlbumArtwork());
        }

        mediaSession.setMetadata(builder.build());

        // The playback is already preparing.
        updateSessionState(PlaybackStateCompat.STATE_CONNECTING);
        isMediaSessionPrepared = true;
        if (tokenConsumer != null && isPrepared) {
            tokenConsumer.accept(mediaSession.getSessionToken());
        }
    }

    private void startPlaceholderForeground() {

        Log.d(TAG, "startPlaceholderForeground: Showing placeholder.");

        Notification notification = notificationBuilder.buildPreparingNotification();
        startForeground(MUSIC_SERVICE_ID, notification);
    }

    @Override
    public void onStateChanged(int oldState, int newState) {
        // TODO: should we move this somewhere else?
        switch (newState) {
            case MediaState.PREPARED:
                onPrepared();
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
                break;
            case MediaState.STOPPED:
                onStopped();
                break;
            default:
                // Nothing.
        }

        // Update notification
        updateNotification();

        if (newState == MediaState.STOPPED) {
            stopSelf();
        }
    }

    /**
     * The media player is read. Show the actual notification and start playback.
     */
    public void onPrepared() {

        updateSessionState(PlaybackStateCompat.STATE_BUFFERING);

        // Get the wakelock.
        if (!wifiLock.isHeld()) {
            wifiLock.acquire();
        }

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result != AudioManager.AUDIOFOCUS_GAIN) {
            stopSelf();
            return; //Failed to gain audio focus
        }

        isPrepared = true;
        if (tokenConsumer != null && isMediaSessionPrepared) {
            tokenConsumer.accept(mediaSession.getSessionToken());
        }
    }

    public void onStopped() {
        updateSessionState(PlaybackStateCompat.STATE_STOPPED);
        if (wifiLock.isHeld()) {
            wifiLock.release();
        }

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(this);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS:
                mediaManager.stop();
                // TODO: stop only after x seconds?
                break;
            case AudioManager.AUDIOFOCUS_REQUEST_FAILED:
                mediaManager.destroy();
                stopSelf();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                mediaManager.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                mediaManager.setVolume(0.5f, 0.5f);
                break;
            case AudioManager.AUDIOFOCUS_GAIN:
                mediaManager.setVolume(1f, 1f);
                break;
        }
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

        // Ensure we absolutely release everything.
        if (wifiLock != null && wifiLock.isHeld()) {
            wifiLock.release();
        }

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(this);

        if (mediaManager != null) {
            mediaManager.destroy();
        }

        stopForeground(true);
    }

    public void setTokenConsumer(Consumer<MediaSessionCompat.Token> tokenConsumer) {
        this.tokenConsumer = tokenConsumer;
        if (isMediaSessionPrepared && isPrepared) {
            tokenConsumer.accept(mediaSession.getSessionToken());
        }
    }

    private MediaNotificationBuilder.MediaInfoProvider provider = new MediaNotificationBuilder.MediaInfoProvider() {
        @Override
        public boolean isPlaying() {
            return mediaManager.isPlaying();
        }

        @Override
        public MediaSessionCompat.Token getMediaToken() {
            return mediaSession.getSessionToken();
        }

        @Override
        public Track getTrack() {
            return track;
        }
    };

    private void updateNotification() {
        // Show the actual notification.
        Notification mediaNotification = notificationBuilder.buildNotification(provider);

        if (mediaManager != null && mediaManager.isPlaying()) {
            startForeground(MUSIC_SERVICE_ID, mediaNotification);
        } else {
            stopForeground(false);
            // Update the notification anyway
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(MUSIC_SERVICE_ID, mediaNotification);
        }
    }

    public MediaManager getMediaManager() {
        return mediaManager;
    }
}