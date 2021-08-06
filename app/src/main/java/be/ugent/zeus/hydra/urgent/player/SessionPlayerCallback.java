/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra.urgent.player;

import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static be.ugent.zeus.hydra.urgent.player.MediaStateListener.State.*;

/**
 * Connects a {@link Player} to a {@link MediaSessionCompat} with the callback, in one direction: it passes updates from
 * the player to the session.
 *
 * @author Niko Strijbol
 * @see PlayerSessionCallback for the reverse mapping.
 */
class SessionPlayerCallback implements MediaStateListener, MetadataListener {

    private static final String TAG = "SessionPlayerCallback";

    private final PlaybackStateCompat.Builder stateCompatBuilder = new PlaybackStateCompat.Builder()
            .setActions(PlaybackStateCompat.ACTION_PAUSE |
                    PlaybackStateCompat.ACTION_PLAY |
                    PlaybackStateCompat.ACTION_STOP);

    /**
     * The session we use.
     */
    private final MediaSessionCompat session;

    private final SessionPlayerServiceCallback serviceCallback;

    /**
     * This will register the state with the session.
     *
     * @param session         The session.
     * @param serviceCallback The service callback.
     */
    SessionPlayerCallback(@NonNull MediaSessionCompat session, @NonNull SessionPlayerServiceCallback serviceCallback) {
        this.session = session;
        this.serviceCallback = serviceCallback;
        // Set the initial state.
        session.setPlaybackState(stateCompatBuilder.build());
    }

    @Override
    public void onStateChanged(int oldState, int newState) {
        Log.d(TAG, "onStateChanged: " + oldState + " -> " + newState);
        switch (newState) {
            case PREPARED:
            case PREPARING:
                updateSessionState(PlaybackStateCompat.STATE_BUFFERING);
                break;
            case ERROR:
                updateSessionState(PlaybackStateCompat.STATE_ERROR);
                break;
            case PAUSED:
                updateSessionState(PlaybackStateCompat.STATE_PAUSED);
                break;
            case PLAYBACK_COMPLETED:
            case STOPPED:
                updateSessionState(PlaybackStateCompat.STATE_STOPPED);
                break;
            case STARTED:
                updateSessionState(PlaybackStateCompat.STATE_PLAYING);
                break;
            case END:
                updateSessionState(PlaybackStateCompat.STATE_NONE);
                break;
            case IDLE:
            case INITIALIZED:
            default:
                // Nothing.
        }
    }

    private void updateSessionState(@PlaybackStateCompat.State int state) {
        stateCompatBuilder.setState(state, 0, 1f);
        session.setPlaybackState(stateCompatBuilder.build());
        serviceCallback.onSessionStateChanged(state);
    }

    @Override
    public void onMetadataUpdate(@Nullable MediaMetadataCompat metadataCompat) {
        session.setMetadata(metadataCompat);
        serviceCallback.onMetadataUpdate();
    }
}
