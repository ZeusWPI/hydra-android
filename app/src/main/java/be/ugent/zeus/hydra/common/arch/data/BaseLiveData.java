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

package be.ugent.zeus.hydra.common.arch.data;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

/**
 * A basic live data, that supports requesting a refresh of the data.
 *
 * @author Niko Strijbol
 */
public abstract class BaseLiveData<R> extends LiveData<R> {

    /**
     * Set this argument to {@code true} in the arguments for a request to bypass any potential cache. Note that this
     * is more of a suggestion than a requirement: the underling data provider may still return cached data if it deems
     * it appropriate, e.g. when there is no network.
     */
    public static final String REFRESH_COLD = "be.ugent.zeus.hydra.data.refresh.cold";
    @Nullable
    private Bundle queuedRefresh;

    /**
     * Same as {@link #flagForRefresh(Bundle)}, using {@link Bundle#EMPTY} as argument.
     */
    public void flagForRefresh() {
        flagForRefresh(Bundle.EMPTY);
    }

    /**
     * Flag this data for a refresh. If there are active observers, the data is reloaded immediately. If there
     * are no active observers, the data will be reloaded when the next active observer registers.
     * <p>
     * If there are no active observers, the {@code args} are saved and will be used when reloading the data at a later
     * point. This method will discard any args from previous calls to this method.
     *
     * @param args The arguments to pass to the {@link #loadData(Bundle)} function.
     */
    public void flagForRefresh(@NonNull Bundle args) {
        Bundle newArgs = new Bundle(args);
        newArgs.putBoolean(REFRESH_COLD, true);
        if (hasActiveObservers()) {
            loadData(newArgs);
        } else {
            this.queuedRefresh = newArgs;
        }
    }

    @Override
    protected void onActive() {
        super.onActive();
        if (queuedRefresh != null) {
            loadData(queuedRefresh);
            queuedRefresh = null;
        }
    }

    /**
     * Load the actual data.
     *
     * @param bundle The arguments for the request.
     */
    protected abstract void loadData(@NonNull Bundle bundle);

    /**
     * @see #loadData(Bundle)
     */
    protected void loadData() {
        this.loadData(Bundle.EMPTY);
    }

    @FunctionalInterface
    public interface OnRefreshStartListener {

        /**
         * Starts when the refresh begins.
         */
        void onRefreshStart();
    }
}