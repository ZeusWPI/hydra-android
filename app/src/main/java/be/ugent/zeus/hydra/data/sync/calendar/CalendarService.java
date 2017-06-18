package be.ugent.zeus.hydra.data.sync.calendar;

import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.Intent;
import android.os.IBinder;
import be.ugent.zeus.hydra.data.sync.SyncAdapterService;

/**
 * Service for a {@link CalendarAdapter}.
 *
 * @author Niko Strijbol
 */
public class CalendarService extends SyncAdapterService {

    @Override
    protected AbstractThreadedSyncAdapter getAdapter() {
        return new CalendarAdapter(getApplicationContext(), true);
    }
}