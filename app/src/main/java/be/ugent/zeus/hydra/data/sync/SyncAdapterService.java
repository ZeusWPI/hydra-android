package be.ugent.zeus.hydra.data.sync;

import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import be.ugent.zeus.hydra.data.sync.course.CourseAdapter;

/**
 * @author Niko Strijbol
 */
public abstract class SyncAdapterService extends Service {

    // Storage for an instance of the sync adapter
    private static AbstractThreadedSyncAdapter adapter;
    // Object to use as a thread-safe lock
    private static final Object lock = new Object();

    @Override
    public void onCreate() {
        /*
         * Create the sync adapter as a singleton.
         * Set the sync adapter as syncable
         * Disallow parallel syncs
         */
        synchronized (lock) {
            if (adapter == null) {
                adapter = getAdapter();
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return adapter.getSyncAdapterBinder();
    }

    /**
     * @return The sync adapter.
     */
    protected abstract AbstractThreadedSyncAdapter getAdapter();
}
