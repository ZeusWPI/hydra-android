package be.ugent.zeus.hydra.urgent.player;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import java8.lang.Iterables;
import java8.util.stream.IntStreams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static be.ugent.zeus.hydra.urgent.player.MediaState.*;

/**
 * Manages playing the audio. This class encapsulates the logic for working with the {@link MediaPlayer}, although
 * that is not part of the public API: the class might choose another playback method.
 *
 * The class handles all things needed for playback, including locks (WiFi and CPU) and audio focus requests.
 *
 * The traditional android terminology is used for the actions that can be taken: you can call play, pause and stop.
 *
 * Because we are working with a live stream, pausing does not make sense: instead, they are redefined to things
 * that make sense for streaming: play will start and prepare the stream, pause will stop the stream, and stop will
 * destroy the stream completely, after which you can no longer call play.
 *
 * Implementation notes: this class has a fair amount of calls to {@link #checkStates(int...)}. This method will
 * check that the state of the media player is actually what you expect it to be. This is a development tool.
 *
 * @author Niko Strijbol
 */
public class Playback implements
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnPreparedListener
{
    private static final String TAG = Playback.class.getSimpleName();

    private static final String WIFI_LOCK_TAG = "UrgentMusic";

    @MediaState
    private int state = MediaState.IDLE;

    private MediaPlayer mediaPlayer;

    private final Context context;
    private final List<MediaStateListener> listeners = new ArrayList<>();
    private final WifiManager.WifiLock wifiLock;
    private final AudioManager.OnAudioFocusChangeListener audioFocusChangeListener;
    private final AudioManager audioManager;

    public Playback(Context context, AudioManager.OnAudioFocusChangeListener audioFocusChangeListener) {
        Context applicationContext = context.getApplicationContext();
        this.context = applicationContext;
        this.audioFocusChangeListener = audioFocusChangeListener;
        this.wifiLock = ((WifiManager) applicationContext.getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, WIFI_LOCK_TAG);
        this.audioManager = (AudioManager) applicationContext.getSystemService(Context.AUDIO_SERVICE);
    }

    public void addMediaStateListener(MediaStateListener listener) {
        this.listeners.add(listener);
    }

    public void removeMediaStateListener(MediaStateListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * Request that the player begins playing. Whatever is currently playing will be stopped.
     *
     * This will also request audio focus. If that fails, nothing will happen.
     *
     * @param metadataCompat The metadata. The player will extract the URL to play from it.
     */
    public void play(MediaMetadataCompat metadataCompat) {

        // Try to get audio focus. If that fails, we stop now.
        int result = audioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return;
        }

        // Ensures the media player is in IDLE state.
        Log.d(TAG, "createMediaPlayerIfNeeded. needed? " + (mediaPlayer == null));
        if (mediaPlayer == null || state == END) {
            mediaPlayer = new MediaPlayer();

            // Make sure the media player will acquire a wake-lock while
            // playing. If we don't do that, the CPU might go to sleep while the
            // song is playing, causing playback to stop.
            mediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);

            // we want the media player to notify us when it's ready preparing,
            // and when it's done playing:
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
        } else {
            mediaPlayer.reset();
        }

        // We don't notify listeners in this state, because this makes the notification disappear. Ideally we would
        // do something about it in the service, but it is way easier to do it here.
        setState(IDLE, false);

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(metadataCompat.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI));
        } catch (IOException e) {
            setState(ERROR);
            return;
        }

        setState(INITIALIZED);

        // Start preparing (buffering and such).
        mediaPlayer.prepareAsync();
        // Get the WiFi-lock.
        wifiLock.acquire();

        setState(PREPARING);
    }

    @MediaState
    public int getState() {
        return state;
    }

    /**
     * Check if the internal state is one of the given states.
     *
     * @param states One of these.
     *
     * @return True or false.
     */
    boolean isStateOneOf(@MediaState int... states) {
        return IntStreams.of(states).anyMatch(i -> i ==  getState());
    }

    /**
     * Stop playback.
     */
    public void stop(boolean destroy) {

        // Stop playback if it is playing.
        if (isStateOneOf(PREPARED, STARTED, STOPPED, PAUSED, PLAYBACK_COMPLETED)) {
            mediaPlayer.stop();
            setState(STOPPED);
        }

        // Stop the WiFi lock
        if (wifiLock.isHeld()) {
            wifiLock.release();
        }

        // Stop the audio focus
        audioManager.abandonAudioFocus(audioFocusChangeListener);

        if (destroy && mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;

            setState(END);
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
        mediaPlayer.start();
        setState(STARTED);
    }

    /**
     * Update the internal state. The {@link #listeners} will be called if present.
     *
     * @param newState The new state.
     */
    private void setState(@MediaState int newState) {
        setState(newState, true);
    }

    /**
     * Update the internal state. The {@link #listeners} will be called if present.
     *
     * @param newState The new state.
     */
    private void setState(@MediaState int newState, boolean notify) {
        int old = state;
        this.state = newState;
        if (notify) {
            Iterables.forEach(listeners, l -> l.onStateChanged(old, state));
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
}