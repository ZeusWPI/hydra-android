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

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static be.ugent.zeus.hydra.urgent.player.MediaStateListener.State.*;

/**
 * Listen to changes of the state of the {@link Player}.
 *
 * @author Niko Strijbol
 */
@FunctionalInterface
public interface MediaStateListener {

    /**
     * Called when the state changes.
     *
     * @param oldState The previous state.
     * @param newState The current state.
     */
    void onStateChanged(@State int oldState, @State int newState);

    /**
     * Google hates enums, so let's use ints.
     *
     * @author Niko Strijbol
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({IDLE, INITIALIZED, PREPARING, PREPARED, STARTED, STOPPED, PAUSED, PLAYBACK_COMPLETED, END, ERROR})
    @interface State {
        int IDLE = 0;
        int INITIALIZED = 1;
        int PREPARING = 2;
        int PREPARED = 3;
        int STARTED = 4;
        int STOPPED = 5;
        int PAUSED = 6;
        int PLAYBACK_COMPLETED = 7;
        int END = 8;
        int ERROR = 9;
    }
}