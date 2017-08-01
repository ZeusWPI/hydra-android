package be.ugent.zeus.hydra.repository.data;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
     * point. This method will discard any args from previous calls to this method.
     *
     * @param context The context, used to send the broadcast.
     * @param args The arguments to pass to the {@link #loadData(Bundle)} function.
     */
    public void flagForRefresh(Context context, @NonNull Bundle args) {
        Bundle newArgs = new Bundle(args);
        newArgs.putBoolean(RefreshBroadcast.REFRESH_COLD, true);
        if (hasActiveObservers()) {
            loadData(newArgs);
        } else {
            this.queuedRefresh = newArgs;
            this.refreshContext = context.getApplicationContext();
        }
    }

    @Override
    protected void onActive() {
        super.onActive();
        if (queuedRefresh != null) {
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
}