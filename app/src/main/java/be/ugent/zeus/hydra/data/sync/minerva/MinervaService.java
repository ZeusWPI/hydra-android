package be.ugent.zeus.hydra.data.sync.minerva;

import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.Intent;
import android.os.IBinder;

/**
 * Minerva synchronisation service.
 *
 * @author Niko Strijbol
 */
public class MinervaService extends Service {

    // Storage for an instance of the sync adapter
    private static AbstractThreadedSyncAdapter adapter;
    // Object to use as a thread-safe lock
    private static final Object lock = new Object();

    @Override
    public void onCreate() {
        /*
         * Create the sync adapter as a singleton.
         */
        synchronized (lock) {
            if (adapter == null) {
                adapter = new MinervaAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return adapter.getSyncAdapterBinder();
    }
}