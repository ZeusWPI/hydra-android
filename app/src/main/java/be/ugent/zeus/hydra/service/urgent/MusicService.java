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
import java8.util.Objects;
import java8.util.function.Consumer;

/**
 * TODO: handle noisy audio
 * @author Niko Strijbol
 */
public class MusicService extends Service implements MediaStateListener, AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = "MusicService";
    private static final int MUSIC_SERVICE_ID = 1;
    private static final String WIFI_LOCK_TAG = "UrgentMusic";

    private final IBinder binder = new MusicBinder(this);

    private MediaNotificationBuilder notificationBuilder;
    private Track track;

    private MediaSessionCompat mediaSession;
    private WifiManager.WifiLock wifiLock;
    private MediaManager mediaManager;
    private boolean initialized = false;
    private boolean isForeground = false;
    private Consumer<MediaSessionCompat.Token> tokenConsumer;
    private boolean isMediaSessionPrepared = false;
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
            mediaManager.setUrl(s);
            if (tokenConsumer != null) {
                tokenConsumer.accept(mediaSession.getSessionToken());
                tokenConsumer = null;
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
        mediaSession.setCallback(new SimpleSessionCallback(getApplicationContext(), mediaManager));
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
        if (tokenConsumer != null && mediaManager.hasUrl()) {
            tokenConsumer.accept(mediaSession.getSessionToken());
            tokenConsumer = null;
        }
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
            case MediaState.END:
                stopSelf(); // Stop the service.
            default:
                // Nothing.
        }

        // Do not update the notification when we stop the service.
        // Normally isForeground should always be false, but we never know.
        if (isForeground || newState != MediaState.END) {
            // Update notification
            updateNotification();
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
        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            stopSelf();
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
                mediaSession.getController().getTransportControls().stop();
                break;
            case AudioManager.AUDIOFOCUS_REQUEST_FAILED:
                mediaSession.getController().getTransportControls().stop();
                stopSelf(); // Stop the service
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                mediaSession.getController().getTransportControls().pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                mediaManager.setVolume(0.5f, 0.5f);
                break;
            case AudioManager.AUDIOFOCUS_GAIN:
                mediaManager.setVolume(1f, 1f);
                mediaSession.getController().getTransportControls().play();
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

        Log.d(TAG, "onDestroy");

        // Ensure we absolutely release everything.
        if (wifiLock != null && wifiLock.isHeld()) {
            wifiLock.release();
        }

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(this);

        if (mediaManager != null && mediaManager.hasMediaPlayer()) {
            mediaManager.destroy();
        }

        stopForeground(true);
        isForeground = false;
    }

    public void setTokenConsumer(Consumer<MediaSessionCompat.Token> tokenConsumer) {
        if (isMediaSessionPrepared && mediaManager.hasUrl()) {
            tokenConsumer.accept(mediaSession.getSessionToken());
        } else {
            this.tokenConsumer = tokenConsumer;
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

        @Override
        public PlaybackStateCompat getState() {
            return mediaSession.getController().getPlaybackState();
        }

    };

    private void updateNotification() {
        // Show the actual notification.
        Notification mediaNotification = notificationBuilder.buildNotification(provider);

        if (mediaManager != null && mediaManager.isPlaying()) {
            startForeground(MUSIC_SERVICE_ID, mediaNotification);
            isForeground = true;
        } else {
            if (isForeground) {
                stopForeground(false);
                isForeground = false;
            }
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(MUSIC_SERVICE_ID, mediaNotification);
        }
    }

    MediaManager getMediaManager() {
        return mediaManager;
    }
}