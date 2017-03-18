package be.ugent.zeus.hydra.ui.common.recyclerview;

import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;

import java8.util.function.BiFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic adapter for a collection of elements {@code D}. This adapter will change the items using
 * {@link android.support.v7.util.DiffUtil.DiffResult}, which will be calculated on a different thread.
 *
 * @param <D> The type of items used in the adapter.
 * @param <VH> The type of the view holder used by the adapter.
 *
 * @author Niko Strijbol
 */
public abstract class DiffAdapter<D, VH extends RecyclerView.ViewHolder> extends Adapter<D, VH> {

    private final Handler handler = new Handler();
    protected final Object updateLock = new Object();
    protected List<D> scheduledUpdate;

    private final BiFunction<List<D>, List<D>, DiffUtil.Callback> callback;

    /**
     * Initialize with a custom callback.
     *
     * @param callback The callback to use.
     */
    protected DiffAdapter(BiFunction<List<D>, List<D>, DiffUtil.Callback> callback) {
        this.callback = callback;
    }

    /**
     * Initialise with the default callback, {@link EqualsCallback}.
     */
    protected DiffAdapter() {
        this(EqualsCallback::new);
    }

    /**
     * Schedule a DiffResult calculation with given items.
     *
     * @param items The new items.
     */
    protected void setInternal(List<D> items) {
        final List<D> oldItems = new ArrayList<>(this.items);

        new Thread(() -> {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback.apply(oldItems, items), true);
            handler.post(() -> applyDiffResult(items, result));
        }).start();
    }

    /**
     * Apply diff results and execute the next scheduled diff if present.
     *
     * @param newItems The new items.
     * @param diffResult The diff result.
     */
    @WorkerThread
    private void applyDiffResult(List<D> newItems, DiffUtil.DiffResult diffResult) {
        dispatchUpdates(newItems, diffResult);
        // If there is scheduled one next, execute it.
        synchronized (updateLock) {
            if (scheduledUpdate != null) {
                setInternal(scheduledUpdate);
                scheduledUpdate = null;
            }
        }
    }

    /**
     * Actually sets the new items and applies the diff result. If you need to do something
     * before setting the new items, this is the place to do it (although you should consider the
     * effects of this method running on a background thread).
     *
     * @param newItems The new items.
     * @param diffResult The diff result.
     */
    @WorkerThread
    protected void dispatchUpdates(List<D> newItems, DiffUtil.DiffResult diffResult) {
        this.items = newItems;
        diffResult.dispatchUpdatesTo(this);
    }

    /**
     * Set the items. When calling this method while an update is being executed, the update
     * will be scheduled. If an update is already scheduled, the new update will replace the old one.
     *
     * Changing the list of items after calling this function is undefined behaviour and strongly discouraged.
     * The adapter itself does reserve the right to edit the list.
     *
     * @param items The new items.
     */
    @MainThread
    public void setItems(List<D> items) {
        synchronized (updateLock) {
            if (scheduledUpdate != null) {
                scheduledUpdate = items;
                return;
            }
        }
        setInternal(items);
    }
}