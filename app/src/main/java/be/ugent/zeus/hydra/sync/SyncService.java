package be.ugent.zeus.hydra.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * @author Niko Strijbol
 * @version 16/08/2016
 */
public class SyncService extends Service {

    public static final String MINERVA_AUTHORITY = "be.ugent.zeus.hydra.minerva.provider";

    // Storage for an instance of the sync adapter
    private static MinervaSyncAdapter adapter;
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
                adapter = new MinervaSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return adapter.getSyncAdapterBinder();
    }
}
