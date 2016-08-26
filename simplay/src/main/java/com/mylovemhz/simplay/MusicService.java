package com.mylovemhz.simplay;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Service to play music on the background, even when the app is closed.
 */
public class MusicService extends Service implements
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnPreparedListener,
        AudioManager.OnAudioFocusChangeListener
{

    private static final String TAG = "MusicService";

    private static final String WIFI_LOCK_TAG = "simplayMusic";

    public static final int ID_NOTIFICATION = 1;
    public static final int TIME_FFWD_RWD = 10 * 1000;

    public static final String RATIONALE_WAKE_LOCK =
            "The music player needs to keep your phone from going into sleep mode " +
                    "to prevent interrupting the music.";

    public static final int REQUEST_PERMISSION_INTERNET = 0;
    public static final int REQUEST_PERMISSION_WAKE_LOCK = 1;
    public static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 2;

    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_REWIND = "action_rewind";
    public static final String ACTION_FAST_FORWARD = "action_fast_forward";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_STOP = "action_stop";
    public static final String ACTION_FINISH = "action_finish";

    public enum State {
        IDLE, INITIALIZED, PREPARING, PREPARED, STARTED,
        STOPPED, PAUSED, COMPLETED, END, ERROR
    }

    private MediaPlayer mediaPlayer;
    private MediaSessionCompat mediaSession;
    private State currentState;
    private WifiManager.WifiLock wifiLock;
    @Nullable
    private MusicCallback callbacks;
    @Nullable
    private BoundServiceCallback boundCallback;

    private boolean isInitialized = false;
    @DrawableRes
    private int smallIcon;
    private PendingIntent contentIntent;

    private TrackManager trackManager;

    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    private final Set<Target> temporaryTargets = new HashSet<>();

    private final IBinder binder = new MusicBinder(this);

    /**
     * Initialise the media player.
     */
    private void initialise() {
        trackManager = new TrackManager();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);

        //Set the wake locks.
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, WIFI_LOCK_TAG);

        ComponentName receiver = new ComponentName(getPackageName(), MediaButtonEventReceiver.class.getName());
        mediaSession = new MediaSessionCompat(this, TAG, receiver, null);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setCallback(new SimpleSessionCallback(this));

        currentState = State.IDLE;
        mediaSession.setActive(true);

        isInitialized = true;
        Log.d(TAG, "Initialized.");
    }

    public TrackManager getTrackManager() {
        if(isInitialized) {
            return trackManager;
        } else {
            throw new IllegalStateException("The service is not initialised yet.");
        }
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    /**
     * Check if we have permission.
     *
     * @param permission The permission.
     *
     * @return True if there is permission, false otherwise.
     */
    public boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void setBoundCallback(BoundServiceCallback callback) {
        this.boundCallback = callback;
    }

    /**
     * @return The current state of the service.
     */
    public State getCurrentState() {
        return currentState;
    }

    public void setContentIntent(PendingIntent contentIntent) {
        this.contentIntent = contentIntent;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isInitialized) {
            initialise();
        }
        handleIntent(intent);
        return START_STICKY;
    }

    public void startPlaying() throws Exception {
        try {
            queueTrack(trackManager.currentTrack());
        } catch (Exception e) {
            stop();
            throw e;
        }
    }

    /**
     * Handle intent actions.
     *
     * @param intent The intent.
     */
    private void handleIntent(Intent intent) {

        if (intent == null || intent.getAction() == null) {
            return;
        }

        String action = intent.getAction();

        switch (action) {
            case ACTION_PLAY:
                play();
                return;
            case ACTION_PAUSE:
                pause();
                return;
            case ACTION_FAST_FORWARD:
                fastForward();
                return;
            case ACTION_REWIND:
                rewind();
                return;
            case ACTION_PREVIOUS:
                previous();
                return;
            case ACTION_NEXT:
                next();
                return;
            case ACTION_STOP:
                stop();
                return;
            case ACTION_FINISH:
                finish();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "Prepared.");
        currentState = State.PREPARED;
        updateSessionState(PlaybackStateCompat.STATE_BUFFERING);
        showNotification();
        play();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (!mp.isLooping()) {
            currentState = State.COMPLETED;
            if(trackManager.hasNext()) {
                next();
            }else{
                stop();
            }
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        currentState = State.ERROR;
        mp.reset();
        currentState = State.IDLE;
        if(callbacks != null) callbacks.onError();
        return true;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "On destroy.");
        stop();
        mediaPlayer.release();
        mediaPlayer = null;
        currentState = State.END;
        mediaSession.release();
    }

    /**
     * Cue a track for playing.
     *
     * @param track The track to play. If this is null, nothing will happen.
     *
     * @throws IOException
     */
    private void queueTrack(Track track) throws IOException {
        Log.d(TAG, "Cue Track...");
        stop();

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result != AudioManager.AUDIOFOCUS_GAIN) {
            return; //Failed to gain audio focus
        }

        if (getCurrentState() == State.STOPPED) {
            mediaPlayer.reset();
            currentState = State.IDLE;
        }

        if (getCurrentState() == State.IDLE) {
            if (track == null) {
                return; //nothing to play
            }
            if (hasPermission(Manifest.permission.WAKE_LOCK)) {
                if (!wifiLock.isHeld()) {
                    wifiLock.acquire();
                }

                mediaPlayer.setDataSource(track.getUrl());
                currentState = State.INITIALIZED;
                mediaPlayer.prepareAsync();
                currentState = State.PREPARING;
                if(callbacks != null) {
                    callbacks.onLoading();
                }
            } else {
                Log.e(TAG, "need permission " + Manifest.permission.WAKE_LOCK);
                if (callbacks != null) {
                    callbacks.onPermissionRequired(
                            REQUEST_PERMISSION_WAKE_LOCK,
                            Manifest.permission.WAKE_LOCK,
                            RATIONALE_WAKE_LOCK);
                }
            }
        }
    }

    private void updateSessionState(int state) {
        int position = 0;
        if (currentState == State.STARTED || currentState == State.PAUSED) {
            position = mediaPlayer.getCurrentPosition();
        }
        PlaybackStateCompat.Builder builder = new PlaybackStateCompat.Builder();
        builder.setState(state, position, 1f);
        mediaSession.setPlaybackState(builder.build());
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS:
                stop();
                break;
            case AudioManager.AUDIOFOCUS_REQUEST_FAILED:
                finish();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                lowerVolume();
                break;
            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
            case AudioManager.AUDIOFOCUS_GAIN:
                restoreVolume();
                break;
        }
    }

    private void lowerVolume() {
        mediaPlayer.setVolume(0.5f, 0.5f);
    }

    private void restoreVolume() {
        mediaPlayer.setVolume(1f, 1f);
    }

    public void play() {
        Log.d(TAG, "play()");
        if (currentState == State.PREPARED || currentState == State.PAUSED) {
            mediaPlayer.start();
            currentState = State.STARTED;
            updateSessionState(PlaybackStateCompat.STATE_PLAYING);
            showNotification();
            if(callbacks != null) {
                callbacks.onPlaybackStarted();
            }
        }
        if (currentState == State.STOPPED || currentState == State.COMPLETED) {
            try {
                Log.d(TAG, "queueing");
                queueTrack(trackManager.currentTrack());
            } catch (IOException e) {
                stop();
                Log.w(TAG, "Error while queueing to play.");
            }
        }
    }

    public void pause() {
        Log.d(TAG, "Paused.");
        if (currentState == State.STARTED) {
            mediaPlayer.pause();
            currentState = State.PAUSED;
            updateSessionState(PlaybackStateCompat.STATE_PAUSED);
            showNotification();
        }
    }

    public void stop() {
        Log.d(TAG, "Stopped playback.");

        if (currentState == State.STARTED || currentState == State.PAUSED || currentState == State.PREPARED || currentState == State.COMPLETED) {
            mediaPlayer.stop();
            currentState = State.STOPPED;
            updateSessionState(PlaybackStateCompat.STATE_STOPPED);
            showNotification();
            if (wifiLock.isHeld()) {
                wifiLock.release();
            }

            if(callbacks != null) {
                callbacks.onPlaybackStopped();
            }
        }
    }

    public void finish() {

        //If the service is bound and blocking, don't kill everything.
        if(boundCallback != null && !boundCallback.canUnbind()) {
            return;
        }

        if (boundCallback != null) {
            boundCallback.requestUnbind();
        }

        Log.d(TAG, "Finishing.");
        if(currentState != State.STOPPED) {
            stop();
        }
        trackManager.releaseTracks();
        removeNotification();
        stopSelf();
    }

    public void next() {
        if (trackManager.hasNext()) {
            if (currentState == State.STARTED || currentState == State.PAUSED) {
                mediaPlayer.stop();
                currentState = State.STOPPED;
                updateSessionState(PlaybackStateCompat.STATE_STOPPED);
            }
            if (currentState == State.STOPPED || currentState == State.COMPLETED) {
                mediaPlayer.reset();
                currentState = State.IDLE;
                updateSessionState(PlaybackStateCompat.STATE_STOPPED);
                showNotification();
            }
            if (currentState == State.IDLE) {
                try {
                    updateSessionState(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT);
                    queueTrack(trackManager.next());
                } catch (Exception e) {
                    stop();
                }
            }
        }
    }

    public void previous() {
        if (trackManager.hasPrevious()) {
            if (currentState == State.STARTED || currentState == State.PAUSED) {
                mediaPlayer.stop();
                currentState = State.STOPPED;
                updateSessionState(PlaybackStateCompat.STATE_STOPPED);
            }
            if (currentState == State.STOPPED) {
                mediaPlayer.reset();
                currentState = State.IDLE;
                updateSessionState(PlaybackStateCompat.STATE_STOPPED);
                showNotification();
            }
            if (currentState == State.IDLE) {
                try {
                    updateSessionState(PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS);
                    queueTrack(trackManager.previous());
                } catch (Exception e) {
                    stop();
                }
            }
        }
    }

    public void fastForward() {
        if (currentState == State.STARTED || currentState == State.PAUSED ||
                currentState == State.PREPARED || currentState == State.COMPLETED) {
            if (mediaPlayer.getCurrentPosition() < mediaPlayer.getDuration() + TIME_FFWD_RWD) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + TIME_FFWD_RWD);
                updateSessionState(PlaybackStateCompat.STATE_FAST_FORWARDING);
            }
        }
    }

    public void seekTo(int position){
        if (currentState == State.STARTED || currentState == State.PAUSED ||
                currentState == State.PREPARED || currentState == State.COMPLETED) {
                mediaPlayer.seekTo(position);
                updateSessionState(PlaybackStateCompat.STATE_FAST_FORWARDING);
        }
    }

    public void rewind() {
        if (currentState == State.STARTED || currentState == State.PAUSED ||
                currentState == State.PREPARED || currentState == State.COMPLETED) {
            if (mediaPlayer.getCurrentPosition() > TIME_FFWD_RWD) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - TIME_FFWD_RWD);
                updateSessionState(PlaybackStateCompat.STATE_REWINDING);
            }
        }
    }

    public MediaSessionCompat.Token getMediaSessionToken() {
        return mediaSession.getSessionToken();
    }

    private NotificationCompat.Action generateAction(int icon, String title, String intentAction) {
        Intent intent = new Intent(getApplicationContext(), MusicService.class);
        intent.setAction(intentAction);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        return new NotificationCompat.Action.Builder(icon, title, pendingIntent).build();
    }

    private void showNotification() {
        updateMetadata();

        Track track;
        try {
            track = trackManager.currentTrack();
        } catch (IllegalStateException e) {
            return;
        }

        Intent intent = new Intent(getApplicationContext(), MusicService.class);
        intent.setAction(ACTION_FINISH);
        PendingIntent cancelIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        NotificationCompat.MediaStyle style = new NotificationCompat.MediaStyle(builder);
        style.setMediaSession(getMediaSessionToken());
        style.setShowCancelButton(true);
        style.setCancelButtonIntent(cancelIntent);

        //Position of the play pause button.
        int playPause = 0;

        if (trackManager.hasPrevious()) {
            builder.addAction(generateAction(R.drawable.noti_ic_skip_previous_24dp, "Previous", ACTION_PREVIOUS)); //0
            playPause++;
        }

        if (isPlaying()) {
            builder.addAction(generateAction(R.drawable.noti_ic_pause_24dp, "Pause", ACTION_PAUSE)); //1
        } else {
            builder.addAction(generateAction(R.drawable.noti_ic_play_arrow_24dp, "Play", ACTION_PLAY)); // 1
        }
        style.setShowActionsInCompactView(playPause);

        if (trackManager.hasNext()) {
            builder.addAction(generateAction(R.drawable.noti_ic_skip_next_24dp, "Next", ACTION_NEXT)); //2
        }

        builder.setSmallIcon(smallIcon)
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
                        temporaryTargets.remove(this);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        publishNotification(builder);
                        temporaryTargets.remove(this);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                temporaryTargets.add(artTarget);
                Picasso.with(this)
                        .load(track.getArtworkUrl())
                        .into(artTarget);
            } catch (IllegalArgumentException e) {
                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.id.albumImage);
                builder.setLargeIcon(icon);
                //no artwork. Ignore.
                publishNotification(builder);
            }
        }else{
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.id.albumImage);
            builder.setLargeIcon(icon);
            publishNotification(builder);
        }
    }

    private void publishNotification(NotificationCompat.Builder builder) {

        if (contentIntent != null) {
            builder.setContentIntent(contentIntent);
        } else {
            Log.e(TAG, "Did you forget to setContentIntent()? Nothing will happen when you touch the notification.");
        }

        try {
            Notification notification = builder.build();

            startForeground(ID_NOTIFICATION, notification);

            if (!isPlaying()) {
                stopForeground(false);
            }

        } catch (IllegalStateException e) {
            Log.e(TAG, "Did you forget to setSmallIconResource() in your onBind()?");
        }
    }

    public PlaybackStateCompat getPlaybackState() {
        return mediaSession.getController().getPlaybackState();
    }

    public boolean isPlaying() {
        if(getPlaybackState() == null) {
            return false;
        }
        int state = getPlaybackState().getState();
        return state == PlaybackStateCompat.STATE_BUFFERING ||
                state == PlaybackStateCompat.STATE_FAST_FORWARDING ||
                state == PlaybackStateCompat.STATE_PLAYING ||
                state == PlaybackStateCompat.STATE_REWINDING;
    }

    /**
     * Set the small notification icon.
     *
     * @param resId The icon.
     */
    public void setSmallIconResource(@DrawableRes int resId) {
        this.smallIcon = resId;
    }

    private void updateMetadata() {
        if(getCurrentState() == State.PREPARED) {
            Track track;
            try {
                track = trackManager.currentTrack();
                MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
                builder.putText(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, track.getArtworkUrl());
                builder.putText(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, track.getArtist());
                builder.putText(MediaMetadataCompat.METADATA_KEY_TITLE, track.getTitle());
                builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mediaPlayer.getDuration());
                mediaSession.setMetadata(builder.build());
            } catch (IllegalStateException e) {
                //nothing to update
            }
        }
    }

    private void removeNotification() {
        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        manager.cancel(ID_NOTIFICATION);
        stopForeground(true);
    }

    public void setCallback(MusicCallback callback) {
        this.callbacks = callback;
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_WAKE_LOCK) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    queueTrack(trackManager.currentTrack());
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                    stop();
                }
            } else {
                //TODO cannot get wifi lock
                stop();
            }
        }
    }

    /**
     * Wrapper to check if this service is running.
     *
     * @param context The context.
     *
     * @return True if it is, otherwise false.
     */
    public static boolean isRunning(Context context) {
        return ServiceUtils.isMyServiceRunning(context, MusicService.class);
    }
}