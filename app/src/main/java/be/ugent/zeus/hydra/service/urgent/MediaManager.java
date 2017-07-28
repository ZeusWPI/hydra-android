package be.ugent.zeus.hydra.service.urgent;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import java8.util.stream.IntStreams;

import java.io.IOException;
import java.util.Arrays;

import static be.ugent.zeus.hydra.service.urgent.MediaState.*;

/**
 * Manages the {@link android.media.MediaPlayer}. The MediaManager supports playing a single stream, and does not
 * have support for multiple tracks.
 *
 * This class will manage the CPU wakelock, but not the wifi wakelock.
 *
 * @author Niko Strijbol
 */
public class MediaManager implements
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnPreparedListener
{
    @MediaState
    private int state = MediaState.IDLE;

    private MediaPlayer mediaPlayer;

    private final Context context;
    private final MediaStateListener listener;

    private String url;
    private MediaPlayer.OnPreparedListener onPrepared;

    public MediaManager(Context context, @Nullable MediaStateListener listener1) {
        this.context = context;
        this.listener = listener1;
    }

    /**
     * Prepare the media player for streaming. This is executed in a blocking manner.
     *
     * @param url The URL to play. If null, the existing URL will be used.
     * @param onPrepared Custom callback to be run when the media player is done preparing. Calling this method will
     *                   clear any existing listener.
     */
    public void prepare(@Nullable String url, @Nullable MediaPlayer.OnPreparedListener onPrepared) throws IOException {

        if (url != null) {
            this.url = url;
        }
        this.onPrepared = onPrepared;

        if (this.url == null) {
            throw new NullPointerException("The URL cannot be null!");
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
            mediaPlayer.setDataSource(this.url);
            setState(INITIALIZED);
        }

        checkStates(INITIALIZED, STOPPED);

        mediaPlayer.prepareAsync();
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
     * Start playback. You MUST have called {@link #prepare(String, android.media.MediaPlayer.OnPreparedListener)} before calling this method.
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
        checkStates(PREPARED, STARTED, STOPPED, PAUSED, PLAYBACK_COMPLETED);
        mediaPlayer.stop();
        setState(STOPPED);
    }

    /**
     * Destroy playback.
     */
    public void destroy() {
        // This may be called from any state.
        mediaPlayer.release();
        setState(END);
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
        if (onPrepared != null) {
            onPrepared.onPrepared(mediaPlayer);
            onPrepared = null;
        }
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