package be.ugent.zeus.hydra.data.sync.calendar;

import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.Intent;
import android.os.IBinder;

/**
 * Service for a {@link CalendarAdapter}.
 *
 * @author Niko Strijbol
 */
public class CalendarService extends Service {

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
                adapter = new CalendarAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return adapter.getSyncAdapterBinder();
    }
}