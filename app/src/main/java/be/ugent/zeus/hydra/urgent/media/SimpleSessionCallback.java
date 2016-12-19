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
package be.ugent.zeus.hydra.urgent.media;

import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import be.ugent.zeus.hydra.urgent.MusicService;

/**
 * A simple implementation of a session callback that maps the methods to the service.
 */
public class SimpleSessionCallback extends MediaSessionCompat.Callback {

    public static final String TAG = "SimpleSessionCallback";

    private MusicService musicService;

    public SimpleSessionCallback(MusicService musicService) {
        this.musicService = musicService;
    }

    @Override
    public void onFastForward() {
        Log.d(TAG, "fast forward");
        musicService.fastForward();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "pause");
        musicService.pause();
    }

    @Override
    public void onPlay() {
        Log.d(TAG, "play");
        musicService.play();
    }

    @Override
    public void onRewind() {
        Log.d(TAG, "rewind");
        musicService.rewind();
    }

    @Override
    public void onSeekTo(long pos) {
        Log.d(TAG, "seek to: " + pos);
        musicService.seekTo((int) pos);
    }

    @Override
    public void onSkipToNext() {
        Log.d(TAG, "skip to next");
        musicService.next();
    }

    @Override
    public void onSkipToPrevious() {
        Log.d(TAG, "skip to previous");
        musicService.previous();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "stop");
        musicService.stop();
    }
}