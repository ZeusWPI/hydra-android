package be.ugent.zeus.hydra.common.ui.recyclerview.adapters;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.AnyThread;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.recyclerview.extensions.ListAdapter;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Manages the data for an adapter.
 *
 * @author Niko Strijbol
 */
public class DataContainer<D> {

    // Threading-related stuff.
    private final Executor backgroundExecutor = getDefaultBackgroundExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Executor mainThreadExecutor = handler::post;

    // Data stuff.
    @Nullable
    private List<D> internalData = null;
    @NonNull
    private List<D> readOnly = Collections.emptyList();

    private final ListUpdateCallback callback;

    /**
     * @param callback The callback to which updates will be dispatched.
     */
    DataContainer(ListUpdateCallback callback) {
        this.callback = callback;
    }

    // Max generation of currently scheduled runnable
    private int maxScheduledGeneration;

    /**
     * Submit an update to the data.
     *
     * @param update The update to apply.
     */
    @MainThread
    public void submitUpdate(AdapterUpdate<D> update) {

        final int generation = ++maxScheduledGeneration;

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
            // Just run execute the work if threading is not allowed.
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

    /**
     * Construct a default background executor.
     * <p>
     * The default executor has a fixed thread pool with 2 threads. This is both the suggested and maximal amount of
     * threads. The thread pool has a bounded queue of size 1. The rejected policy is discarding the oldest first.
     *
     * @return The executor.
     */
    private static Executor getDefaultBackgroundExecutor() {
        return new ThreadPoolExecutor(2, 2,
                0, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1),
                new ThreadPoolExecutor.DiscardOldestPolicy());
    }
}