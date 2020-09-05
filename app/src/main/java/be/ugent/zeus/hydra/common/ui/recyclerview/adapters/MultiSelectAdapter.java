package be.ugent.zeus.hydra.common.ui.recyclerview.adapters;

import android.util.Log;
import android.util.Pair;
import android.util.SparseBooleanArray;
import androidx.annotation.NonNull;

import java.util.*;

import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;

/**
 * Adapter with items that are checkable. An item is considered checked if the state is {@code true}.
 *
 * @author Niko Strijbol
 */
@SuppressWarnings("WeakerAccess")
public abstract class MultiSelectAdapter<H> extends DiffAdapter<H, DataViewHolder<H>> {

    private static final String TAG = "MultiSelectAdapter";
    /**
     * This keeps track of which elements are selected and which are not.
     */
    protected final SparseBooleanArray booleanArray = new SparseBooleanArray();
    private final Collection<Callback<H>> callbacks = new HashSet<>();
    /**
     * The default value for the selection.
     */
    private boolean defaultValue = false;

    /**
     * @return The default state.
     */
    public boolean getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void clear() {
        booleanArray.clear();
        super.clear();
        for (Callback<H> c : callbacks) {
            c.onStateChanged(MultiSelectAdapter.this);
        }
    }

    /**
     * Set the values to use.
     *
     * @param values  The values.
     * @param initial The initial value.
     */
    public void submitData(List<H> values, boolean initial) {
        this.defaultValue = initial;
        this.submitData(values);
        for (Callback<H> c : callbacks) {
            c.onStateChanged(MultiSelectAdapter.this);
        }
    }

    /**
     * Set the values to use.
     *
     * @param values      The values.
     * @param nonInitials The indices of the items of {@code values} that should not receive the default value.
     * @param initial     The initial value.
     */
    public void submitData(List<H> values, Set<Integer> nonInitials, boolean initial) {
        this.defaultValue = initial;
        this.submitData(values);
        for (int index : nonInitials) {
            setChecked(index);
            notifyItemChanged(index);
        }

        for (Callback<H> c : callbacks) {
            c.onStateChanged(MultiSelectAdapter.this);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method will NOT erase or reset the state of the items; use {@link #submitData(List, boolean)} for that.
     */
    @Override
    public void submitData(List<H> items) {
        this.booleanArray.clear();
        super.submitData(items);
    }

    /**
     * Change the boolean value at the given position to the reverse value. This operation DOES NOT trigger a change in
     * the recycler view data, so the methods for changed items is not called. The rationale behind this is that this
     * method is meant to be attached to a checkbox as a listener. Then the checkbox is already updated.
     *
     * @param position The position.
     */
    public void setChecked(int position) {
        Log.d(TAG, "setChecked: " + position + ", current is: " + isChecked(position));
        if (!isChecked(position) == getDefaultValue()) {
            booleanArray.delete(position);
            Log.d(TAG, "setChecked: setting to " + getDefaultValue());
        } else {
            Log.d(TAG, "setChecked: setting to " + !getDefaultValue());
            booleanArray.put(position, !getDefaultValue());
        }
        for (Callback<H> c : callbacks) {
            c.onStateChanged(MultiSelectAdapter.this);
        }
    }

    /**
     * Set all items to the given boolean. This operation DOES trigger a layout update.
     *
     * @param checked The value.
     */
    public void setAllChecked(boolean checked) {
        /*
        Since we are setting every element to the same state, it is more efficient to change the default
        value and remove all saved states. This used to be a O(n) operation, now it is a O(1) operations.
         */
        this.defaultValue = checked;
        this.booleanArray.clear();
        for (Callback<H> c : callbacks) {
            c.onStateChanged(MultiSelectAdapter.this);
        }
        notifyDataSetChanged();
    }

    /**
     * Check if a entry with given position is checked or not.
     *
     * @param position The adapter position of the item.
     * @return The state of the item.
     */
    public boolean isChecked(int position) {
        return booleanArray.get(position, defaultValue);
    }

    public Iterable<Pair<H, Boolean>> getItemsAndState() {
        return () -> new Iterator<Pair<H, Boolean>>() {

            private int current = 0;

            @Override
            public boolean hasNext() {
                return current < getItemCount();
            }

            @Override
            public Pair<H, Boolean> next() {
                Pair<H, Boolean> value = new Pair<>(getItem(current), isChecked(current));
                current++;
                return value;
            }
        };
    }

    /**
     * Set items and their state. Similar to {@link #submitData(List)}, but with a custom state for every item. This can
     * be useful when populating the list if the previous states were saved.
     * <p>
     * If the state of every item is the same, it is more efficient to use {@link #submitData(List, boolean)}.
     *
     * @param values The values to set.
     */
    public void setItemsAndState(List<Pair<H, Boolean>> values) {
        List<H> items = new ArrayList<>();
        submitData(items);
        booleanArray.clear();
        for (int i = 0; i < values.size(); i++) {
            Pair<H, Boolean> value = values.get(i);
            items.add(value.first);
            if (value.second != getDefaultValue()) {
                booleanArray.append(i, value.second);
            }
        }
        for (Callback<H> c : callbacks) {
            c.onStateChanged(MultiSelectAdapter.this);
        }
    }

    /**
     * Returns true if at least one element has a state of {@code true}.
     *
     * @return True if there is at least one selected element.
     */
    public boolean hasSelected() {
        return getDefaultValue() || booleanArray.size() > 0;
    }

    /**
     * @return Number of selected items.
     */
    public int selectedSize() {
        if (getDefaultValue()) {
            return getItemCount();
        } else {
            return booleanArray.size();
        }
    }

    /**
     * @return A read only collections of all the items that have state {@code true}.
     */
    public Collection<H> getSelectedItems() {
        List<H> list = new ArrayList<>();
        if (getDefaultValue()) {
            return dataContainer.getData();
        } else {
            for (int i = 0; i < booleanArray.size(); i++) {
                list.add(getItem(booleanArray.keyAt(i)));
            }
        }
        return Collections.unmodifiableCollection(list);
    }

    /**
     * Adds a new callback. If the callback already exists, the behaviour is safe, but undefined. This means the
     * callback could be registered again and called twice, or the calls with an existing callback could be ignored.
     * The only guarantee is that there will be no exception and the callback will be called at least once.
     * <p>
     * There is no guarantee in which order the callbacks will be called.
     *
     * @param callback The callback to add.
     */
    public void addCallback(@NonNull Callback<H> callback) {
        this.callbacks.add(callback);
    }

    /**
     * Removes a callback. If the callback is not registered, this does nothing.
     * <p>
     * If the callback is registered twice, the behaviour is also safe, but undefined (same as {@link #addCallback(Callback)}.
     * The method may remove all instance or might remove one instance.
     *
     * @param callback The callback to remove.
     */
    public void removeCallback(@NonNull Callback<H> callback) {
        this.callbacks.remove(callback);
    }

    @FunctionalInterface
    public interface Callback<H> {

        /**
         * Is called when the state changes. There is no guarantee when this will be called: when multiple items
         * are changed, it might be called for each item, or only once.
         *
         * @param adapter Can be used by the client to query things.
         */
        void onStateChanged(MultiSelectAdapter<H> adapter);
    }
}
