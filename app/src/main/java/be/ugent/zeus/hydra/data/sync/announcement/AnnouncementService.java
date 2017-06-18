package be.ugent.zeus.hydra.data.sync.announcement;

import android.content.AbstractThreadedSyncAdapter;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import be.ugent.zeus.hydra.data.sync.SyncAdapterService;

/**
 * Minerva synchronisation service.
 *
 * @author Niko Strijbol
 */
public class AnnouncementService extends SyncAdapterService {

    @Override
    protected AbstractThreadedSyncAdapter getAdapter() {
        return new AnnouncementAdapter(getApplicationContext(), true);
    }
}