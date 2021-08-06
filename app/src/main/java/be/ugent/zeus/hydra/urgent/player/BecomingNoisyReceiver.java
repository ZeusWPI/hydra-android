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

import android.content.*;
import android.media.AudioManager;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;

/**
 * Helper class for listening for when headphones are unplugged (or the audio
 * will otherwise cause playback to become "noisy").
 */
class BecomingNoisyReceiver extends BroadcastReceiver {

    private final Context context;
    private final IntentFilter noisyIntentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private final MediaControllerCompat controller;

    private boolean registered = false;

    BecomingNoisyReceiver(Context context, MediaSessionCompat mediaSession) {
        this.context = context;
        this.controller = new MediaControllerCompat(context, mediaSession);
    }

    void register() {
        if (!registered) {
            context.registerReceiver(this, noisyIntentFilter);
            registered = true;
        }
    }

    void unregister() {
        if (registered) {
            context.unregisterReceiver(this);
            registered = false;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
            controller.getTransportControls().pause();
        }
    }
}