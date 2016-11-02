/*
 * Copyright 2016 Allan Pichardo
 * Copyright 2016 Niko Strijbol
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.ugent.zeus.hydra.urgent;

import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import be.ugent.zeus.hydra.Manifest;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.urgent.media.MediaAction;
import be.ugent.zeus.hydra.urgent.media.MediaButtonEventReceiver;
import be.ugent.zeus.hydra.urgent.media.MediaNotificationManager;
import be.ugent.zeus.hydra.urgent.media.SimpleSessionCallback;
import be.ugent.zeus.hydra.urgent.track.Track;
import be.ugent.zeus.hydra.urgent.track.TrackManager;

import java.io.IOException;

/**
 * The music is played in a foreground service, enabling people to listen to the stream even when the app itself
 * is closed or killed.
 *
 * @author Niko Strijbol
 * @author allanpichardo
 */
public class MusicService extends Service implements
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnPreparedListener,
        AudioManager.OnAudioFocusChangeListener,
        MediaNotificationManager.NotificationListener,
        MediaNotificationManager.MediaInfoProvider
{
    private static final String TAG = "MusicService";

    private static final String WIFI_LOCK_TAG = "simplayMusic";

    private static final int TIME_FFWD_RWD = 10 * 1000;

    public static final int REQUEST_PERMISSION_INTERNET = 0;
    public static final int REQUEST_PERMISSION_WAKE_LOCK = 1;
    public static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 2;

    @MediaState
    private int state;

    private MediaPlayer mediaPlayer;
    private MediaSessionCompat mediaSession;
    private WifiManager.WifiLock wifiLock;
    @Nullable
    private MusicCallback callbacks;
    @Nullable
    private BoundServiceCallback boundCallback;

    private boolean isInitialized = false;

    private TrackManager trackManager;

    private MediaNotificationManager notificationManager;

    private final IBinder binder = new MusicBinder(this);

    @Override
    public void onCancelNotification(int id) {
        stopForeground(true);
    }

    @Override
    public void onPublishNotification(int id, Notification notification) {
        startForeground(id, notification);

        if (!isPlaying()) {
            stopForeground(false);
        }
    }

    /**
     * Initialise the media player and other stuff.
     */
    private void initialise() {
        //Add objects for various tasks
        trackManager = new TrackManager();
        mediaPlayer = new MediaPlayer();
        notificationManager = new MediaNotificationManager(getApplicationContext(), this);

        //Initialize the right mode on the media player and attach the service.
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

        state = MediaState.IDLE;
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
    @MediaState
    public int getCurrentState() {
        return state;
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

        MediaAction.doAction(intent.getAction(), this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "Prepared.");
        state = MediaState.PREPARED;
        updateSessionState(PlaybackStateCompat.STATE_BUFFERING);
        notificationManager.show(this);
        play();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (!mp.isLooping()) {
            state = MediaState.COMPLETED;
            if (trackManager.hasNext()) {
                next();
            } else {
                stop();
            }
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        state = MediaState.ERROR;
        mp.reset();
        state = MediaState.IDLE;
        if(callbacks != null) {
            callbacks.onError();
        }
        return true;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "On destroy.");
        stop();
        mediaPlayer.release();
        mediaPlayer = null;
        state = MediaState.END;
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

        if (getCurrentState() == MediaState.STOPPED) {
            mediaPlayer.reset();
            state = MediaState.IDLE;
        }

        if (getCurrentState() == MediaState.IDLE) {

            if (track == null) {
                return; //nothing to play
            }

            track.getUrl(new Track.UrlConsumer() {
                @Override
                public void receive(@Nullable String url) throws IOException {
                    if (url == null) {
                        return; //nothing to play
                    }
                    if (hasPermission(Manifest.permission.WAKE_LOCK)) {
                        if (!wifiLock.isHeld()) {
                            wifiLock.acquire();
                        }

                        mediaPlayer.setDataSource(url);
                        state = MediaState.INITIALIZED;
                        mediaPlayer.prepareAsync();
                        state = MediaState.PREPARING;
                        if(callbacks != null) {
                            callbacks.onLoading();
                        }
                    } else {
                        Log.e(TAG, "need permission " + Manifest.permission.WAKE_LOCK);
                        if (callbacks != null) {
                            callbacks.onPermissionRequired(
                                    REQUEST_PERMISSION_WAKE_LOCK,
                                    Manifest.permission.WAKE_LOCK,
                                    getString(R.string.urgent_wifi_lock_description));
                        }
                    }
                }
            });
        }
    }

    private void updateSessionState(@PlaybackStateCompat.State int state) {
        int position = 0;
        if (getCurrentState() == MediaState.STARTED || getCurrentState() == MediaState.PAUSED) {
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

        if (getCurrentState() == MediaState.PREPARED || getCurrentState() == MediaState.PAUSED) {
            mediaPlayer.start();
            state = MediaState.STARTED;
            updateSessionState(PlaybackStateCompat.STATE_PLAYING);
            notificationManager.show(this);
            if(callbacks != null) {
                callbacks.onPlaybackStarted();
            }
        }
        if (getCurrentState() == MediaState.STOPPED || getCurrentState() == MediaState.COMPLETED) {
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
        if (getCurrentState() == MediaState.STARTED) {
            mediaPlayer.pause();
            state = MediaState.PAUSED;
            updateSessionState(PlaybackStateCompat.STATE_PAUSED);
            notificationManager.show(this);
        }
    }

    /**
     * Stop playback.
     */
    public void stop() {
        Log.d(TAG, "Stopped playback.");

        if (hasLoadedTrack()) {
            moveToStop();

            if (wifiLock.isHeld()) {
                wifiLock.release();
            }

            if(callbacks != null) {
                callbacks.onPlaybackStopped();
            }
        }
    }

    /**
     * Terminate playback. If the service is bound, don't do anything. If not bound, stop playback, and then stop
     * the service.
     */
    public void finish() {

        if (boundCallback != null) {
            boundCallback.requestUnbind();
        }

        Log.d(TAG, "Finishing.");

        moveToStop();

        trackManager.releaseTracks();
        notificationManager.remove();
        stopSelf();
    }

    /**
     * Play the next queued track. If there is no next track, this method will have no effect.
     */
    public void next() {
        //If there are no previous tracks.
        if(!trackManager.hasNext()) {
            return;
        }

        //Clean up
        moveToIdle();

        try {
            updateSessionState(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT);
            queueTrack(trackManager.next());
        } catch (Exception e) { //TODO
            stop();
        }
    }

    /**
     * Play the track before the current one in the queue. If there is no previous track, this method will have no
     * effect.
     */
    public void previous() {
        //There is no previous track.
        if(!trackManager.hasPrevious()) {
            return;
        }
        //Clean up
        moveToIdle();

        try {
            updateSessionState(PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS);
            queueTrack(trackManager.previous());
        } catch (Exception e) { //TODO
            stop();
        }
    }

    /**
     * Move the current state to idle. This function will take care of all necessary steps, i.e. stop any playing media,
     * resetting the media player, etc.
     *
     * If the state could not be set to idle after the necessary steps, e.g. because the state is
     * {@link MediaState#END}, an exception is thrown.
     */
    private void moveToIdle() {
        //Stop any playing song
        moveToStop();

        //At this point, the state is always stopped.
        //Remove any media and reset the media player.
        mediaPlayer.reset();
        state = MediaState.IDLE;
        updateSessionState(PlaybackStateCompat.STATE_STOPPED);
        //We only update the notification once.
        notificationManager.show(this);

        //If the state is not idle right now, we have an exception!
        if(getCurrentState() != MediaState.IDLE) {
            throw new IllegalStateException("The state could not be set to idle.");
        }
    }

    /**
     * Move the current state to stopped. This function will take care of all necessary steps, i.e. stop any playing media,
     * resetting the media player, etc.
     *
     * If the state could not be set to stopped after the necessary steps, e.g. because the state is
     * {@link MediaState#END}, an exception is thrown.
     */
    private void moveToStop() {
        //Stop any playing song
        if(hasLoadedTrack()) {
            mediaPlayer.stop();
            state = MediaState.STOPPED;
            updateSessionState(PlaybackStateCompat.STATE_STOPPED);
            notificationManager.show(this);
        }

        if(getCurrentState() != MediaState.STOPPED) {
            throw new IllegalStateException("The state could not be set to stopped.");
        }
    }

    public void fastForward() {
        if (hasLoadedTrack()) {
            if (mediaPlayer.getCurrentPosition() < mediaPlayer.getDuration() + TIME_FFWD_RWD) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + TIME_FFWD_RWD);
                updateSessionState(PlaybackStateCompat.STATE_FAST_FORWARDING);
            }
        }
    }

    public void seekTo(int position){
        if (hasLoadedTrack()) {
            mediaPlayer.seekTo(position);
            updateSessionState(PlaybackStateCompat.STATE_FAST_FORWARDING);
        }
    }

    public void rewind() {
        if (hasLoadedTrack()) {
            if (mediaPlayer.getCurrentPosition() > TIME_FFWD_RWD) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - TIME_FFWD_RWD);
                updateSessionState(PlaybackStateCompat.STATE_REWINDING);
            }
        }
    }

    /**
     * Returns true if the current {@link #state} is one of the following:
     *
     * - {@link MediaState#STARTED}
     * - {@link MediaState#PAUSED}
     * - {@link MediaState#PREPARED}
     * - {@link MediaState#COMPLETED}
     *
     * When this is true, there is a loaded track in the {@link MediaPlayer}.
     *
     * @return True if there is a track.
     */
    private boolean hasLoadedTrack() {
        return getCurrentState() == MediaState.STARTED ||
                getCurrentState() == MediaState.PAUSED ||
                getCurrentState() == MediaState.PREPARED ||
                getCurrentState() == MediaState.COMPLETED;
    }


    /**
     * @return The state of the media session.
     */
    private PlaybackStateCompat getPlaybackState() {
        return mediaSession.getController().getPlaybackState();
    }

    /**
     * @return True if the service is playing music.
     */
    public boolean isPlaying() {

        if(!hasLoadedTrack() || mediaSession.getController().getPlaybackState() == null) {
            return false;
        }

        @PlaybackStateCompat.State
        final int state = getPlaybackState().getState();

        return state == PlaybackStateCompat.STATE_BUFFERING ||
                state == PlaybackStateCompat.STATE_FAST_FORWARDING ||
                state == PlaybackStateCompat.STATE_PLAYING ||
                state == PlaybackStateCompat.STATE_REWINDING;
    }

    @Override
    public MediaSessionCompat.Token getMediaToken() {
        return mediaSession.getSessionToken();
    }

    public void updateMetadata() {
        if(getCurrentState() == MediaState.PREPARED) {
            Track track;
            try {
                track = trackManager.currentTrack();
                final MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
                builder.putText(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, track.getArtist());
                builder.putText(MediaMetadataCompat.METADATA_KEY_TITLE, track.getTitle());
                builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mediaPlayer.getDuration());
                builder.putText(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, track.getArtworkUrl());

                mediaSession.setMetadata(builder.build());
            } catch (IllegalStateException e) {
                //nothing to update
            }
        }
    }

    @NonNull
    public MediaNotificationManager getNotificationManager() {
        if(isInitialized) {
            return notificationManager;
        } else {
            throw new IllegalStateException("The service must be initialized first");
        }
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