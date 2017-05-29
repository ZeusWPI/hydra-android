package be.ugent.zeus.hydra.data.network;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

/**
 * @author Niko Strijbol
 *
 * @deprecated Use the {@link be.ugent.zeus.hydra.data.network.exceptions.PartialDataException} instead.
 */
@Deprecated
public class OfflineBroadcaster {

    public static final String OFFLINE = "be.ugent.zeus.hydra.network.offline";

    static void broadcastNetworkError(Context context) {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
        manager.sendBroadcast(new Intent(OFFLINE));
    }

    /**
     * Get the intent filter for network IO problems.
     *
     * @return The filter.
     */
    public static IntentFilter getBroadcastFilter() {
        return new IntentFilter(OFFLINE);
    }
}