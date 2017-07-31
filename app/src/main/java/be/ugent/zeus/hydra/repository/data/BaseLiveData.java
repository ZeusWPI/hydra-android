package be.ugent.zeus.hydra.repository.data;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import be.ugent.zeus.hydra.repository.RefreshBroadcast;

/**
 * A basic live data, that supports requesting a refresh of the data.
 *
 * @author Niko Strijbol
 */
public abstract class BaseLiveData<R> extends LiveData<R> {

    private Bundle queuedRefresh;
    private Context refreshContext;

    public static final String REFRESH_MANUAL = "be.ugent.zeus.hydra.data.refresh.manual";

    /**
     * Same as {@link #flagForRefresh(Context, Bundle)}, using {@link Bundle#EMPTY} as argument.
     */
    public void flagForRefresh(Context context) {
        flagForRefresh(context, Bundle.EMPTY);
    }

    /**
     * Flag this data for a refresh. If there are active observers, the data is reloaded immediately. If there
     * are no active observers, the data will be reloaded when the next active observer registers.
     *
     * If there are no active observers, the {@code args} are saved and will be used when reloading the data at a later
     * point. This method will disgard any args from previous calls to this method.
     *
     * When the data is reload, a broadcast {@link be.ugent.zeus.hydra.repository.RefreshBroadcast} will be sent. This
     * is for compatability with the broadcast refresh system and might be removed.
     *
     * @param context The context, used to send the broadcast.
     * @param args The arguments to pass to the {@link #loadData(Bundle)} function.
     */
    public void flagForRefresh(Context context, @NonNull Bundle args) {
        if (hasActiveObservers()) {
            sendBroadcast(context, args);
            loadData(args);
        } else {
            this.queuedRefresh = args;
            this.refreshContext = context.getApplicationContext();
        }
    }

    @Override
    protected void onActive() {
        super.onActive();
        if (queuedRefresh != null) {
            sendBroadcast(refreshContext, queuedRefresh);
            loadData(queuedRefresh);
            queuedRefresh = null;
            refreshContext = null;
        }
    }

    /**
     * Load the actual data.
     *
     * @param bundle The arguments for the request.
     */
    protected abstract void loadData(@Nullable Bundle bundle);

    private void sendBroadcast(Context context, Bundle args) {
        Intent intent = RefreshBroadcast.buildRefreshIntent(true);
        intent.putExtra(REFRESH_MANUAL, true);
        intent.putExtras(args);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}