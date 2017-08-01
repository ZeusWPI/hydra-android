package be.ugent.zeus.hydra.repository;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import be.ugent.zeus.hydra.repository.data.BaseLiveData;

/**
 * @author Niko Strijbol
 */
@Deprecated
public class RefreshBroadcast {

    public static final String REFRESH_ACTION = "be.ugent.zeus.hydra.data.refresh";
    public static final String REFRESH_COLD = BaseLiveData.REFRESH_COLD;

    public static void broadcastRefresh(Context context, boolean cold) {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
        manager.sendBroadcast(buildRefreshIntent(cold));
    }

    public static Intent buildRefreshIntent(boolean cold) {
        Intent intent = new Intent(REFRESH_ACTION);
        intent.putExtra(REFRESH_COLD, cold);
        return intent;
    }

    /**
     * Get the intent filter for network IO problems.
     *
     * @return The filter.
     */
    public static IntentFilter getBroadcastFilter() {
        return new IntentFilter(REFRESH_ACTION);
    }
}