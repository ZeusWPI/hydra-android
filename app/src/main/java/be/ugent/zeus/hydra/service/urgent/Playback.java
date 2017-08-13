package be.ugent.zeus.hydra.service.urgent;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaMetadataCompat;

import java8.util.stream.IntStreams;

import java.io.IOException;
import java.util.Arrays;

import static be.ugent.zeus.hydra.service.urgent.MediaState.*;

/**
 * Manages actually playing the stream.
 *
 * This class will manage the CPU wakelock, but not the wifi wakelock.
 *
 * @author Niko Strijbol
 */
public class Playback implements
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnPreparedListener
{
    private static final String WIFI_LOCK_TAG = "UrgentMusic";

    @MediaState
    private int state = MediaState.IDLE;

    private MediaPlayer mediaPlayer;

    private final Context context;
    private final MediaStateListener listener;
    private final WifiManager.WifiLock wifiLock;

    public Playback(Context context, @Nullable MediaStateListener listener1) {
        Context applicationContext = context.getApplicationContext();
        this.context = applicationContext;
        this.listener = listener1;
        wifiLock = ((WifiManager) applicationContext.getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, WIFI_LOCK_TAG);
    }


    public void play(MediaMetadataCompat metadataCompat) {

        // If we are already playing or preparing, do nothing.
        if (isStateOneOf(PREPARING)) {
            return;
        }

        if (isStateOneOf(PREPARED, STARTED, PAUSED, PLAYBACK_COMPLETED)) {
            play();
            return;
        }

        // If the state is error or end, we must construct a new media player.
        if (mediaPlayer == null || state == ERROR || state == END) {

            if (mediaPlayer == null || state == END) {
                mediaPlayer = new MediaPlayer();
            } else {
                mediaPlayer.reset();
            }
            setState(IDLE);

            mediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
        }

        if (state == IDLE) {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource(metadataCompat.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI));
            } catch (IOException e) {
                setState(ERROR);
                return;
            }
            setState(INITIALIZED);
        }

        checkStates(INITIALIZED, STOPPED);

        mediaPlayer.prepareAsync();
        wifiLock.acquire();
        setState(PREPARING);
    }

    @MediaState
    public int getState() {
        return state;
    }

    public boolean isStateOneOf(@MediaState int... states) {
        return IntStreams.of(states).anyMatch(i -> i ==  getState());
    }

    /**
     * Start playback.
     */
    public void play() {
        checkStates(PREPARED, STARTED, PAUSED, PLAYBACK_COMPLETED);
        mediaPlayer.start();
        setState(STARTED);
    }

    /**
     * Pause playback.
     */
    public void pause() {
        checkStates(STARTED, PAUSED, PLAYBACK_COMPLETED);
        mediaPlayer.pause();
        setState(PAUSED);
    }

    /**
     * Stop playback.
     */
    public void stop() {
        if (isStateOneOf(PREPARED, STARTED, STOPPED, PAUSED, PLAYBACK_COMPLETED)) {
            mediaPlayer.stop();
            setState(STOPPED);
        }

        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        // This may be called from any state.
        setState(END);

        if (wifiLock.isHeld()) {
            wifiLock.release();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        setState(PLAYBACK_COMPLETED);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        setState(ERROR);
        return false;
    }

    private void checkStates(@MediaState int... allowed) {
        for (int i : allowed) {
            if (i == state) {
                return;
            }
        }

        throw new IllegalStateException("Media player is in wrong state: " + state + ", allowed: " + Arrays.toString(allowed));
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        setState(PREPARED);
        play();
    }

    /**
     * Update the internal state. The {@link #listener} will be called if present.
     *
     * @param newState The new state.
     */
    private void setState(@MediaState int newState) {
        int old = state;
        this.state = newState;
        if (listener != null) {
            listener.onStateChanged(old, state);
        }
    }

    /**
     * Adjust the volume.
     *
     * @param left The volume for left.
     * @param right The volume for right.
     *
     * @see MediaPlayer#setVolume(float, float) for a better description.
     */
    public void setVolume(float left, float right) {
        if (state != ERROR && state != END) {
            mediaPlayer.setVolume(left, right);
        }
    }

    /**
     * Check if playing is active. If in the correct state, the call is delegated to the media player, otherwise false.
     *
     * @return Is music playing or not.
     */
    public boolean isPlaying() {
        return state != ERROR && state != END && mediaPlayer.isPlaying();
    }
}