package be.ugent.zeus.hydra.urgent.player;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.media.AudioAttributesCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import java9.util.stream.IntStream;

import java.util.Objects;

import static be.ugent.zeus.hydra.urgent.player.MediaStateListener.State.*;

/**
 * @author Niko Strijbol
 */
public class Player {

    private static final String TAG = "Player";

    private static final float MEDIA_VOLUME_DEFAULT = 1.0f;
    private static final float MEDIA_VOLUME_DUCK = 0.2f;

    /**
     * Indicates if the player should start playing if the state becomes {@link be.ugent.zeus.hydra.urgent.player.MediaStateListener.State#PREPARED}.
     *
     * This is modifiable in this package.
     */
    boolean shouldPlayWhenReady = false;

    /**
     * The internal media player.
     */
    private final InternalPlayer mediaPlayer;

    /**
     * The internal audio manager.
     */
    private final AudioManager audioManager;

    /**
     * The internal audio attributes.
     */
    private final AudioAttributesCompat audioAttributes;

    /**
     * Listener for audio focus changes.
     */
    private final AudioManager.OnAudioFocusChangeListener focusChangeListener;

    /**
     * Internal request.
     */
    private AudioFocusRequest audioFocusRequest;

    private final UrgentTrackProvider provider;

    private final Context context;

    /**
     * The volume we want. This is changed as a response to audio focus things.
     */
    private float volume = MEDIA_VOLUME_DEFAULT;

    private Player(Context context, AudioManager manager, AudioAttributesCompat audioAttributes, UrgentTrackProvider provider) {
        this.provider = provider;
        this.context = context;
        this.mediaPlayer = new InternalPlayer(context);
        this.audioManager = manager;
        this.audioAttributes = audioAttributes;
        this.focusChangeListener = new AudioFocusListener(this);
        this.mediaPlayer.addListener((oldState, newState) -> {
            if (newState == PREPARED && shouldPlayWhenReady) {
                start();
            }
        });
    }

    private boolean isStateOneOf(@MediaStateListener.State int... states) {
        int state = mediaPlayer.getState();
        return IntStream.of(states)
                .anyMatch(s -> s == state);
    }

    /**
     * Set the volume and start playing.
     */
    private void start() {
        maybeUpdateVolume();
        mediaPlayer.start();
    }

    /**
     * Sets the {@link #shouldPlayWhenReady} property to {@code true}. If the player is in the appropriate state,
     * {@link InternalPlayer#start()} will be called. Otherwise, the playing will be scheduled.
     */
    private void playOrSchedulePlay() {
        shouldPlayWhenReady = true;
        Log.d(TAG, "playOrSchedulePlay: state is " + mediaPlayer.getState());
        // If the state is end or error, create a new player.
        if (isStateOneOf(END, ERROR)) {
            mediaPlayer.createNew(context);
            volume = MEDIA_VOLUME_DEFAULT;
        }
        // If the state is idle, schedule a track update.
        if (isStateOneOf(IDLE)) {
            // Set audio types
            mediaPlayer.setAudioAttributes(audioAttributes);
            // This will propagate to a call to prepare.
            provider.prepareMedia(this::receiveTrackInformation);
            return;
        }

        if (isStateOneOf(STOPPED)) {
            mediaPlayer.prepareAsync();
            return;
        }

        if (isStateOneOf(PREPARED, STARTED, PAUSED, PLAYBACK_COMPLETED)) {
            start();
        }
    }

    /**
     * Cancel playing or stop playing if the player is playing.
     */
    private void cancelOrStopPlay() {
        shouldPlayWhenReady = false;
        if (isStateOneOf(PREPARED, STARTED, PAUSED, PLAYBACK_COMPLETED)) {
            mediaPlayer.stop();
        }
    }

    /**
     * Receive track information. If {@link #shouldPlayWhenReady} is {@code true}, the player will be prepared as well.
     *
     * @param track The track to play.
     */
    void receiveTrackInformation(@Nullable MediaMetadataCompat track) {
        if (isStateOneOf(IDLE)) {
            mediaPlayer.setDataSource(track);
            if (shouldPlayWhenReady && isStateOneOf(INITIALIZED)) {
                mediaPlayer.prepareAsync();
            }
        } else {
            Log.w(TAG, "Ignoring data, player is not ready. State of player is " + mediaPlayer.getState());
        }
    }

    /**
     * Set to true to start playing, set to false to stop playing. Inspired by ExoPlayer.
     *
     * @param playWhenReady True to start or schedule playing, false to cancel or stop playing.
     */
    @SuppressWarnings("WeakerAccess")
    public void setPlayWhenReady(boolean playWhenReady) {
        Log.d(TAG, "setPlayWhenReady: play? = " + playWhenReady);
        if (playWhenReady) {
            requestAudioFocus();
            playOrSchedulePlay();
        } else {
            cancelOrStopPlay();
            abandonAudioFocus();
        }
    }

    void setDefaultVolume() {
        this.volume = MEDIA_VOLUME_DEFAULT;
        maybeUpdateVolume();
    }

    void setDuckVolume() {
        this.volume = MEDIA_VOLUME_DUCK;
        maybeUpdateVolume();
    }

    private void maybeUpdateVolume() {
        if (isStateOneOf(IDLE, INITIALIZED, STOPPED, PREPARED, STARTED, PAUSED, PLAYBACK_COMPLETED)) {
            mediaPlayer.setVolume(this.volume);
        }
    }

    /**
     * Destroy the player.
     */
    public void destroy() {
        setPlayWhenReady(false);
        mediaPlayer.release();
        mediaPlayer.nullify();
    }

    private void requestAudioFocus() {
        int result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            result = requestAudioFocusOreo();
        } else {
            //noinspection deprecation
            result = audioManager.requestAudioFocus(focusChangeListener,
                    audioAttributes.getLegacyStreamType(),
                    AudioManager.AUDIOFOCUS_GAIN);
        }

        // Call the listener whenever focus is granted - even the first time!
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            shouldPlayWhenReady = true;
            focusChangeListener.onAudioFocusChange(AudioManager.AUDIOFOCUS_GAIN);
        } else {
            Log.i(TAG, "Playback not started: Audio focus request denied");
        }
    }

    private void abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            abandonAudioFocusOreo();
        } else {
            //noinspection deprecation
            audioManager.abandonAudioFocus(focusChangeListener);
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private int requestAudioFocusOreo() {
        return audioManager.requestAudioFocus(getAudioFocusRequest());
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void abandonAudioFocusOreo() {
        audioManager.abandonAudioFocusRequest(getAudioFocusRequest());
    }

    @TargetApi(Build.VERSION_CODES.O)
    private AudioFocusRequest buildFocusRequest() {
        return new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes((AudioAttributes) Objects.requireNonNull(audioAttributes.unwrap()))
                .setOnAudioFocusChangeListener(focusChangeListener)
                .build();
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private AudioFocusRequest getAudioFocusRequest() {
        if (audioFocusRequest == null) {
            audioFocusRequest = buildFocusRequest();
        }
        return audioFocusRequest;
    }

    public UrgentTrackProvider getProvider() {
        return provider;
    }

    /**
     * Create a new player.
     */
    public static class Builder {

        private final Context context;

        private MediaSessionCompat mediaSession;
        private SessionPlayerServiceCallback serviceCallback1;
        private PlayerSessionServiceCallback serviceCallback2;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder withSession(MediaSessionCompat mediaSession) {
            this.mediaSession = mediaSession;
            return this;
        }

        public Builder withCallback1(SessionPlayerServiceCallback serviceCallback) {
            this.serviceCallback1 = serviceCallback;
            return this;
        }

        public Builder withCallback2(PlayerSessionServiceCallback serviceCallback) {
            this.serviceCallback2 = serviceCallback;
            return this;
        }

        public Player build() {
            // Create some classes we need for the Player.
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            AudioAttributesCompat attributes = new AudioAttributesCompat.Builder()
                    .setUsage(AudioAttributesCompat.USAGE_MEDIA)
                    .setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
                    .build();
            UrgentTrackProvider provider = new UrgentTrackProvider(context);
            Player player = new Player(context.getApplicationContext(), audioManager, attributes, provider);

            // Connect the media session to the player.
            BecomingNoisyReceiver receiver = new BecomingNoisyReceiver(context, mediaSession);
            PlayerSessionCallback callback = new PlayerSessionCallback(player, receiver, serviceCallback2);
            mediaSession.setCallback(callback);

            // Connect the player to the media session.
            SessionPlayerCallback listener = new SessionPlayerCallback(mediaSession, serviceCallback1);
            player.mediaPlayer.addListener(listener);

            return player;
        }
    }
}