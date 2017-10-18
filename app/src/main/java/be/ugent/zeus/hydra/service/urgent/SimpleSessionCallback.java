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
package be.ugent.zeus.hydra.service.urgent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import org.threeten.bp.Instant;
import org.threeten.bp.temporal.ChronoUnit;

/**
 * A simple implementation of a session callback that maps the methods to the {@link Playback}.
 *
 * This is the main controller for the state of the playback: all user-generated events are directed to this class.
 *
 * We handle streaming, not playing songs; this influences how the commands are translated:
 * <ul>
 *     <li>A pause command will result in a 'stop' call. This means the buffering also stops.</li>
 *     <li>A stop command will result in the media player being destroyed.</li>
 * </ul>
 *
 * This class will also handle the {@link AudioManager#ACTION_AUDIO_BECOMING_NOISY} case, by stopping the playback.
 *
 * @author Niko Strijbol.
 */
public class SimpleSessionCallback extends MediaSessionCompat.Callback implements AudioManager.OnAudioFocusChangeListener, MediaStateListener {

    public static final String TAG = "SimpleSessionCallback";

    private final IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private final BecomingNoisyReceiver myNoisyAudioStreamReceiver = new BecomingNoisyReceiver();

    private Playback mediaManager;
    private final Context context;
    private final UrgentTrackProvider provider;
    private boolean registered = false;
    private final Runnable metadataUpdater;
    private Runnable nextUpdate;

    private final Handler handler = new Handler();

    public SimpleSessionCallback(Context context, UrgentTrackProvider provider, Runnable metadataUpdater) {
        this.context = context.getApplicationContext();
        this.provider = provider;
        this.metadataUpdater = metadataUpdater;
    }

    public void setMediaManager(Playback mediaManager) {
        if (this.mediaManager != null) {
            this.mediaManager.removeMediaStateListener(this);
        }
        this.mediaManager = mediaManager;
        this.mediaManager.addMediaStateListener(this);
    }

    @Override
    public void onPause() {
        // Stop the media manager, but do not release everything.
        mediaManager.stop(false);
    }

    @Override
    public void onPlayFromMediaId(String mediaId, Bundle extras) {
        if (provider.isUrgentId(mediaId)) {
            onPlay();
        }
    }

    @Override
    public void onPlay() {
        // Do nothing if we are already preparing or there is nothing to play.
        if (provider.hasTrackInformation()) {
            // Try and get audio focus
            handlePlay();
        } else {
            provider.prepareMedia(success -> {
                if (success) {
                    handlePlay();
                }
            });
        }
    }

    private void handlePlay() {
        mediaManager.play(provider.getTrack());
        scheduleMetadataUpdate();
    }

    @Override
    public void onStop() {
        cancelMetadataUpdate();
        mediaManager.stop(true);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS:
                onStop();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                onPause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                mediaManager.setVolume(0.5f, 0.5f);
                break;
            case AudioManager.AUDIOFOCUS_GAIN:
                mediaManager.setVolume(1f, 1f);
                onPlay();
                break;
        }
    }

    @Override
    public void onStateChanged(int oldState, int newState) {
        // If we are playing, register the receiver, otherwise not.
        if (newState == MediaState.STARTED) {
            if (!registered) {
                context.registerReceiver(myNoisyAudioStreamReceiver, intentFilter);
                registered = true;
            }
        } else {
            if (registered) {
                context.unregisterReceiver(myNoisyAudioStreamReceiver);
                registered = false;
            }
        }
    }

    private class BecomingNoisyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                // Stop the playback
                onStop();
            }
        }
    }

    private void scheduleMetadataUpdate() {
        Log.d(TAG, "scheduleMetadataUpdate");
        cancelMetadataUpdate();
        nextUpdate = getMetadataUpdate();

        Instant time = Instant.now().plus(1, ChronoUnit.HOURS)
                .truncatedTo(ChronoUnit.HOURS)
                .plus(1, ChronoUnit.MINUTES);

        long millis = Instant.now().until(time, ChronoUnit.MILLIS);
        Log.d(TAG, "scheduleMetadataUpdate: scheduling update in " + millis + " millis.");
        handler.postDelayed(nextUpdate, millis);
    }

    private void cancelMetadataUpdate() {
        if (nextUpdate != null) {
            Log.d(TAG, "cancelMetadataUpdate");
            handler.removeCallbacks(nextUpdate);
            nextUpdate = null;
        }
    }

    private Runnable getMetadataUpdate() {
        return () -> provider.prepareMedia(aBoolean -> {
            if (aBoolean && metadataUpdater != null) {
                metadataUpdater.run();
                if (mediaManager.getState() == MediaState.STARTED || mediaManager.getState() == MediaState.PAUSED) {
                    scheduleMetadataUpdate();
                }
            }
        });
    }
}