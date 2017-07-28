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
 * @author Niko Strijbol.
 */
public class SimpleSessionCallback extends MediaSessionCompat.Callback implements MediaPlayer.OnPreparedListener {

    public static final String TAG = "SimpleSessionCallback";

    private MediaManager mediaManager;

    public SimpleSessionCallback(MediaManager mediaManager) {
        this.mediaManager = mediaManager;
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
            }
        } catch (IOException e) {
            Log.e(TAG, "Error while trying to play.", e);
        }
    }

    @Override
    public void onStop() {
        Log.d(TAG, "stop");
        // Stop if necessary/possible
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
    }
}