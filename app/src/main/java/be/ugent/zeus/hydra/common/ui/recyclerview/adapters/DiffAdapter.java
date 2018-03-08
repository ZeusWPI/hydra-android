package be.ugent.zeus.hydra.common.ui.recyclerview.adapters;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;

import be.ugent.zeus.hydra.common.ui.recyclerview.EqualsCallback;
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
@Deprecated
public abstract class DiffAdapter<D, VH extends RecyclerView.ViewHolder> extends Adapter<D, VH> {

    private final Handler handler = new Handler(Looper.getMainLooper());
    protected final Object updateLock = new Object();

    /**
     * The next update. This stores the next items, if we're already calculating a diff when we receive a new
     * update.
     */
    protected List<D> scheduledUpdate;

    /**
     * Indicates if the adapter is currently processing an update or not. If this is true, you should schedule
     * the update and not run it immediately.
     */
    boolean isDiffing = false;

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
     * Schedule a DiffResult calculation with given items. This will calculate the diff result in a separate thread,
     * and deliver the result in the main thread, in the {@link #applyDiffResult(List, DiffUtil.DiffResult)} method.
     * This will also set the {@link #isDiffing} variable to true.
     *
     * The caller of this method should ensure the Thread from a previous call has been finished, or the data may
     * become inconsistent.
     *
     * Most of the time, you should call {@link #setItems(List)} instead.
     *
     * @param items The new items.
     */
    protected void updateItemInternal(List<D> items) {
        synchronized (updateLock) {
            if (isDiffing) {
                throw new IllegalStateException("An update is already being applied!");
            }
            isDiffing = true;
        }

        // Save a copy of the old items.
        final List<D> oldItems = new ArrayList<>(this.items);

        new Thread(() -> {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback.apply(oldItems, items), true);
            handler.post(() -> applyDiffResult(items, result));
        }).start();
    }

    /**
     * Receives the diff result from the Thread in the {@link #updateItemInternal(List)} method. This will
     * dispatch the update to the adapter (this class).
     *
     * After the dispatching of the results, the method will launch the next update, if one is scheduled.
     *
     * @param newItems The new items.
     * @param diffResult The diff result.
     */
    private void applyDiffResult(List<D> newItems, DiffUtil.DiffResult diffResult) {
        // Dispatch the update to the adapter.
        this.items = newItems;
        diffResult.dispatchUpdatesTo(this);

        // If there is scheduled one next, execute it.
        synchronized (updateLock) {
            // We are done with this update.
            isDiffing = false;

            if (scheduledUpdate != null) {
                updateItemInternal(scheduledUpdate);
                scheduledUpdate = null;
            }
        }
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
    public void setItems(List<D> items) {
        synchronized (updateLock) {

            // When using diff util, the recycler view always scrolls to the top after updating.
            // This is very annoying when rotating the device, so don't use it when there are no cards.
            // This is also (marginally) more efficient.
            if (this.items.isEmpty()) {
                this.items = items;
                notifyItemRangeInserted(0, this.getItemCount());
                return;
            }

            // If we are currently updating, schedule the update.
            if (isDiffing) {
                scheduledUpdate = items;
            } else {
                updateItemInternal(items);
            }
        }
    }
}