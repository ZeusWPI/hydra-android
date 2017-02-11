package be.ugent.zeus.hydra.minerva.agenda.sync;

import android.content.Intent;
import android.os.IBinder;

/**
 * Service for a {@link Adapter}.
 *
 * TODO: see if this can abstracted, to prevent code duplication.
 *
 * @author Niko Strijbol
 */
public class Service extends android.app.Service {

    private static Adapter syncAdapter;

    private static final Object lock = new Object();

    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (lock) {
            if (syncAdapter == null) {
                syncAdapter = new Adapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }
}