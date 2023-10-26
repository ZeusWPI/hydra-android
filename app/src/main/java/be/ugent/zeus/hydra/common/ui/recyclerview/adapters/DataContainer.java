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

package be.ugent.zeus.hydra.common.ui.recyclerview.adapters;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * Manages the data for an adapter.
 *
 * @author Niko Strijbol
 */
public class DataContainer<D> {

    private static Executor backgroundExecutorSingleton;
    // Threading-related stuff.
    private final Executor backgroundExecutor = getDefaultBackgroundExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Executor mainThreadExecutor = handler::post;
    private final ListUpdateCallback callback;
    // Data stuff.
    @Nullable
    private List<D> internalData;
    @NonNull
    private List<D> readOnly = Collections.emptyList();
    // Max generation of currently scheduled runnable
    private int maxScheduledGeneration;

    /**
     * @param callback The callback to which updates will be dispatched.
     */
    DataContainer(ListUpdateCallback callback) {
        this.callback = callback;
    }

    /**
     * Construct a default background executor.
     * <p>
     * The default executor has a fixed thread pool with 2 threads. This is both the suggested and maximal amount of
     * threads. The thread pool has a bounded queue of size 1. The rejected policy is discarding the oldest first.
     *
     * @return The executor.
     */
    private static Executor getDefaultBackgroundExecutor() {
        if (backgroundExecutorSingleton == null) {
            backgroundExecutorSingleton = new ThreadPoolExecutor(2, 2,
                    0, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(1),
                    new ThreadPoolExecutor.DiscardOldestPolicy());
        }
        return backgroundExecutorSingleton;
    }

    /**
     * Submit an update to the data.
     *
     * @param update The update to apply.
     */
    @MainThread
    void submitUpdate(AdapterUpdate<D> update) {
        int generation = ++maxScheduledGeneration;

        // Construct a unit of work for the update.
        Runnable work = () -> {
            List<D> newData = update.getNewData(internalData);
            mainThreadExecutor.execute(() -> {
                if (maxScheduledGeneration == generation) {
                    applyResult(newData, update);
                }
            });
        };

        if (update.shouldUseMultiThreading()) {
            // Schedule the update.
            backgroundExecutor.execute(work);
        } else {
            // Just execute the work if threading is not allowed.
            work.run();
        }
    }

    @MainThread
    private void applyResult(@Nullable List<D> newData, AdapterUpdate<D> update) {
        update.applyUpdatesTo(callback);
        internalData = newData;
        if (internalData == null) {
            readOnly = Collections.emptyList();
        } else {
            readOnly = Collections.unmodifiableList(internalData);
        }
    }

    /**
     * @return Get a read-only copy of the current data. When the data in the container is updated,
     * this may or may not be updated.
     */
    @NonNull
    public List<D> getData() {
        return readOnly;
    }
}