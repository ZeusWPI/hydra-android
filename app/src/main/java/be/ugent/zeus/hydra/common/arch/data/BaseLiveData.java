package be.ugent.zeus.hydra.common.arch.data;

import android.arch.lifecycle.LiveData;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * A basic live data, that supports requesting a refresh of the data.
 *
 * @author Niko Strijbol
 */
public abstract class BaseLiveData<R> extends LiveData<R> {

    private Bundle queuedRefresh;
    @Nullable
    private OnRefreshStartListener onRefreshStartListener;

    @Deprecated
    public static final String REFRESH_MANUAL = "be.ugent.zeus.hydra.data.refresh.manual";

    /**
     * Set this to true in the args,
     */
    public static final String REFRESH_COLD = "be.ugent.zeus.hydra.data.refresh.cold";

    /**
     * Same as {@link #flagForRefresh(Bundle)}, using {@link Bundle#EMPTY} as argument.
     */
    public void flagForRefresh() {
        flagForRefresh(Bundle.EMPTY);
    }

    /**
     * Flag this data for a refresh. If there are active observers, the data is reloaded immediately. If there
     * are no active observers, the data will be reloaded when the next active observer registers.
     *
     * If there are no active observers, the {@code args} are saved and will be used when reloading the data at a later
     * point. This method will discard any args from previous calls to this method.
     *
     * @param args The arguments to pass to the {@link #loadData(Bundle)} function.
     */
    public void flagForRefresh(@NonNull Bundle args) {
        Bundle newArgs = new Bundle(args);
        newArgs.putBoolean(REFRESH_COLD, true);
        if (hasActiveObservers()) {
            loadData(newArgs);
            if (onRefreshStartListener != null) {
                onRefreshStartListener.onRefreshStart();
            }
        } else {
            this.queuedRefresh = newArgs;
        }
    }

    @Override
    protected void onActive() {
        super.onActive();
        if (queuedRefresh != null) {
            loadData(queuedRefresh);
            if (onRefreshStartListener != null) {
                onRefreshStartListener.onRefreshStart();
            }
            queuedRefresh = null;
        }
    }

    /**
     * Load the actual data.
     *
     * @param bundle The arguments for the request.
     */
    protected abstract void loadData(@Nullable Bundle bundle);

    @FunctionalInterface
    public interface OnRefreshStartListener {

        /**
         * Starts when the refresh begins.
         */
        void onRefreshStart();
    }

    public void registerRefreshListener(OnRefreshStartListener listener) {
        onRefreshStartListener = listener;
    }
}