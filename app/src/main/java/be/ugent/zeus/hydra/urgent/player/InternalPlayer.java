package be.ugent.zeus.hydra.urgent.player;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.media.AudioAttributesCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static be.ugent.zeus.hydra.urgent.player.MediaStateListener.State.*;

/**
 * Wrapper around {@link android.media.MediaPlayer} to track state.
 *
 * @author Niko Strijbol
 */
class InternalPlayer {

    private static final String TAG = "InternalPlayer";
    /**
     * Listeners for this state.
     */
    private final List<MediaStateListener> listeners = new ArrayList<>();
    /**
     * The current state of the media player.
     */
    @MediaStateListener.State
    private int state = IDLE;
    private MediaPlayer mediaPlayer;

    InternalPlayer(Context context) {
        createNew(context);
    }

    void nullify() {
        checkStateIsOneOf(END);
        Log.d(TAG, "nullify is called");
        this.mediaPlayer = null;
    }

    void createNew(Context context) {
        if (state != END && state != IDLE) {
            mediaPlayer.release();
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            setState(ERROR);
            return false;
        });
        mediaPlayer.setOnPreparedListener(mp -> setState(PREPARED));
        mediaPlayer.setOnCompletionListener(mp -> {
            // Weirdly, this callback is called in the error state?
            if (state != END && state != ERROR) {
                setState(PLAYBACK_COMPLETED);
            }
        });
        setState(IDLE);
    }

    /**
     * Check if the internal state is one of the given states.
     *
     * @param states One of these.
     */
    private void checkStateIsOneOf(@MediaStateListener.State int... states) {
        if (IntStream.of(states).noneMatch(i -> i == state)) {
            throw new IllegalStateException("Illegal state: " + state + ", allowed are " + Arrays.toString(states));
        }
    }

    @MediaStateListener.State
    int getState() {
        return state;
    }

    private void setState(@MediaStateListener.State int newState) {
        Log.i(TAG, "setState: from " + state + " to " + newState);
        if (newState != state) {
            int oldState = this.state;
            this.state = newState;
            for (MediaStateListener l : listeners) {
                l.onStateChanged(oldState, state);
            }
        }
    }

    void addListener(@Nullable MediaStateListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    @SuppressWarnings("unused")
    void removeListener(@Nullable MediaStateListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    void setDataSource(MediaMetadataCompat dataSource) {
        if (dataSource == null) {
            setState(ERROR);
            return;
        }
        try {
            mediaPlayer.setDataSource(dataSource.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI));
            setState(INITIALIZED);
        } catch (IOException e) {
            setState(ERROR);
        }
    }

    void prepareAsync() {
        checkStateIsOneOf(INITIALIZED, STOPPED);
        mediaPlayer.prepareAsync();
        setState(PREPARING);
    }

    void start() {
        checkStateIsOneOf(PREPARED, STARTED, PAUSED, PLAYBACK_COMPLETED);
        mediaPlayer.start();
        setState(STARTED);
    }

    @SuppressWarnings("unused")
    void pause() {
        checkStateIsOneOf(STARTED, PAUSED);
        mediaPlayer.pause();
        setState(PAUSED);
    }

    void stop() {
        checkStateIsOneOf(STARTED, STOPPED, PAUSED, PLAYBACK_COMPLETED, PREPARED);
        mediaPlayer.stop();
        setState(STOPPED);
    }

    void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            setState(END);
        }
    }

    void setVolume(float volume) {
        checkStateIsOneOf(IDLE, INITIALIZED, STOPPED, PREPARED, STARTED, PAUSED, PLAYBACK_COMPLETED);
        mediaPlayer.setVolume(volume, volume);
    }

    void setAudioAttributes(AudioAttributesCompat attributes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(attributes.getContentType())
                    .setFlags(attributes.getFlags())
                    .setUsage(attributes.getUsage())
                    .setLegacyStreamType(attributes.getLegacyStreamType())
                    .build();
            mediaPlayer.setAudioAttributes(audioAttributes);
        } else {
            //noinspection deprecation
            mediaPlayer.setAudioStreamType(attributes.getLegacyStreamType());
        }
    }
}