package be.ugent.zeus.hydra.repository;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

/**
 * @author Niko Strijbol
 */
public class RefreshBroadcast {

    public static final String BROADCAST = "be.ugent.zeus.hydra.data.refresh";
    public static final String REFRESH_COLD = "be.ugent.zeus.hydra.data.refresh.cold";
    public static final String REFRESH_TYPE = "be.ugent.zeus.hydra.data.refresh.type";

    public static final int REFRESH_ALL = -1;

    public static void broadcastRefresh(Context context, boolean cold) {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(BROADCAST);
        intent.putExtra(REFRESH_COLD, cold);
        intent.putExtra(REFRESH_TYPE, REFRESH_ALL);
        manager.sendBroadcast(intent);
    }

    /**
     * Get the intent filter for network IO problems.
     *
     * @return The filter.
     */
    public static IntentFilter getBroadcastFilter() {
        return new IntentFilter(BROADCAST);
    }
}