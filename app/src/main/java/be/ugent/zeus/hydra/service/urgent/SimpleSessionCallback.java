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
import android.media.MediaPlayer;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import java.io.IOException;

/**
 * A simple implementation of a session callback that maps the methods to the {@link MediaManager}.
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
public class SimpleSessionCallback extends MediaSessionCompat.Callback implements MediaPlayer.OnPreparedListener {

    public static final String TAG = "SimpleSessionCallback";

    private final IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private final BecomingNoisyReceiver myNoisyAudioStreamReceiver = new BecomingNoisyReceiver();

    private final MediaManager mediaManager;
    private final Context context;
    private boolean registered = false;

    public SimpleSessionCallback(Context context, MediaManager mediaManager) {
        this.mediaManager = mediaManager;
        this.context = context.getApplicationContext();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "pause");

        if (mediaManager.isStateOneOf(
                MediaState.STARTED,
                MediaState.PAUSED,
                MediaState.PLAYBACK_COMPLETED)) {
            mediaManager.pause();
        }
    }

    @Override
    public void onPlay() {
        Log.d(TAG, "play");
        try {
            // If not in a playable state, prepare the media manager.
            if (!mediaManager.isStateOneOf(
                    MediaState.PREPARED,
                    MediaState.STARTED,
                    MediaState.PAUSED,
                    MediaState.PLAYBACK_COMPLETED)
                    ) {
                mediaManager.prepare(null, this);
            } else {
                mediaManager.play();
                if (!registered) {
                    context.registerReceiver(myNoisyAudioStreamReceiver, intentFilter);
                    registered = true;
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error while trying to play.", e);
        }
    }

    @Override
    public void onStop() {
        Log.d(TAG, "stop");
        ensureStop();
        if (registered) {
            context.unregisterReceiver(myNoisyAudioStreamReceiver);
            registered = false;
        }
    }

    private void ensureStop() {
        if (mediaManager.isStateOneOf(
                MediaState.PREPARED,
                MediaState.STARTED,
                MediaState.STOPPED,
                MediaState.PAUSED,
                MediaState.PLAYBACK_COMPLETED)) {
            mediaManager.stop();
        }

        mediaManager.destroy();
    }

    /**
     * Called when a play command is received, but the player wasn't ready. At that point the prepare() method is
     * called. When preparing finishes, this method is called.
     *
     * @param mediaPlayer The media player. Not intended for use.
     */
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaManager.play();
        if (!registered) {
            context.registerReceiver(myNoisyAudioStreamReceiver, intentFilter);
            registered = true;
        }
    }

    private class BecomingNoisyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                // Stop the playback
                ensureStop();
            }
        }
    }
}