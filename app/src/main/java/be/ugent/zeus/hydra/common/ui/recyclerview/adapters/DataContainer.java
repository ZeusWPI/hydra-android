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
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Manages the data for an adapter.
 *
 * TODO: should we support blocking updates (e.g. for when we are searching?)
 * TODO: replace our threads by a variant on the system used by the Support Libs
 *
 * @author Niko Strijbol
 */
public class DataContainer<D> {

    private final Object updateLock = new Object();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final ListUpdateCallback callback;

    @Nullable
    private List<D> internalData = null;
    @NonNull
    private List<D> readOnly = Collections.emptyList();

    /**
     * An update that is scheduled because the adapter is busy updating or is not accepting update right now.
     */
    private AdapterUpdate<D> scheduledUpdate;

    private boolean isUpdating = false;

    /**
     * @param callback The callback to which updates will be dispatched.
     */
    DataContainer(ListUpdateCallback callback) {
        this.callback = callback;
    }

    /**
     * Submit an update to the data.
     *
     * @param update The update to apply.
     */
    @MainThread
    public void submitUpdate(AdapterUpdate<D> update) {
        synchronized (updateLock) {
            if (isUpdating) {
                scheduledUpdate = update;
                return;
            } else {
                isUpdating = true;
                scheduledUpdate = null;
            }
        }
        executeUpdate(update);
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
        synchronized (updateLock) {
            isUpdating = false;
        }
        if (scheduledUpdate != null) {
            submitUpdate(scheduledUpdate);
            scheduledUpdate = null;
        }
    }

    @MainThread
    private void executeUpdate(AdapterUpdate<D> update) {
        if (update.shouldUseMultiThreading()) {
            Runnable work = () -> {
                List<D> newData = update.getNewData(internalData);
                handler.post(() -> applyResult(newData, update));
            };
            new Thread(work).start();
        } else {
            List<D> newData = update.getNewData(internalData);
            applyResult(newData, update);
        }
    }

    /**
     * @return Get a read-only copy of the current data. When the data in the container is updated, this may or may not
     * be updated.
     */
    @NonNull
    public List<D> getData() {
        return readOnly;
    }
}