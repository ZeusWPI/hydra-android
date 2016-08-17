package be.ugent.zeus.hydra.minerva.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Minerva synchronisation service.
 *
 * @author Niko Strijbol
 */
public class SyncService extends Service {

    // Storage for an instance of the sync adapter
    private static SyncAdapter adapter;
    // Object to use as a thread-safe lock
    private static final Object lock = new Object();

    /**
     * Instantiate the sync adapter object.
     */
    @Override
    public void onCreate() {
        /*
         * Create the sync adapter as a singleton.
         * Set the sync adapter as syncable
         * Disallow parallel syncs
         */
        synchronized (lock) {
            if (adapter == null) {
                adapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return adapter.getSyncAdapterBinder();
    }
}