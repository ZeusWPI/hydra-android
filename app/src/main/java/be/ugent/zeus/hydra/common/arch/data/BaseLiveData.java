package be.ugent.zeus.hydra.common.arch.data;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

/**
 * A basic live data, that supports requesting a refresh of the data.
 *
 * @author Niko Strijbol
 */
public abstract class BaseLiveData<R> extends LiveData<R> {

    /**
     * Set this argument to {@code true} in the arguments for a request to bypass any potential cache. Note that this
     * is more of a suggestion than a requirement: the underling data provider may still return cached data if it deems
     * it appropriate, e.g. when there is no network.
     */
    public static final String REFRESH_COLD = "be.ugent.zeus.hydra.data.refresh.cold";
    @Nullable
    private Bundle queuedRefresh;
    @Nullable
    private OnRefreshStartListener onRefreshStartListener;

    /**
     * Same as {@link #flagForRefresh(Bundle)}, using {@link Bundle#EMPTY} as argument.
     */
    public void flagForRefresh() {
        flagForRefresh(Bundle.EMPTY);
    }

    /**
     * Flag this data for a refresh. If there are active observers, the data is reloaded immediately. If there
     * are no active observers, the data will be reloaded when the next active observer registers.
     * <p>
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
    protected abstract void loadData(@NonNull Bundle bundle);

    /**
     * @see #loadData(Bundle)
     */
    protected void loadData() {
        this.loadData(Bundle.EMPTY);
    }

    public void registerRefreshListener(OnRefreshStartListener listener) {
        onRefreshStartListener = listener;
    }

    @FunctionalInterface
    public interface OnRefreshStartListener {

        /**
         * Starts when the refresh begins.
         */
        void onRefreshStart();
    }
}