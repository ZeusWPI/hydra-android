package be.ugent.zeus.hydra.ui.common.recyclerview.adapters;

import android.util.Pair;
import android.util.SparseBooleanArray;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Adapter with items that are checkable.
 *
 * @author Niko Strijbol
 */
public abstract class MultiSelectListAdapter<H> extends Adapter<H, DataViewHolder<Pair<H, Boolean>>> {

    /**
     * This keeps track of which elements are selected and which are not.
     */
    protected SparseBooleanArray booleanArray = new SparseBooleanArray();
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
    }

    /**
     * Set the values to use.
     *
     * @param values  The values.
     * @param initial The initial value.
     */
    public void setItems(List<H> values, boolean initial) {
        this.defaultValue = initial;
        this.booleanArray.clear();
        this.setItems(values);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method will NOT erase or reset the state of the items; use {@link #setItems(List, boolean)} for that.
     */
    @Override
    public void setItems(List<H> items) {
        super.setItems(items);
    }

    /**
     * Change the boolean value at the given position to the reverse value. This operation DOES NOT trigger a change in
     * the recycler view data, so the methods for changed items is not called. The rationale behind this is that this
     * method is meant to be attached to a checkbox as a listener. Then the checkbox is already updated.
     *
     * @param position The position.
     */
    public void setChecked(int position) {
        this.booleanArray.put(position, !isChecked(position));
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
        notifyDataSetChanged();
    }

    /**
     * Check if a entry with given position is checked or not.
     *
     * @param position The adapter position of the item.
     *
     * @return The state of the item.
     */
    public boolean isChecked(int position) {
        return booleanArray.get(position, defaultValue);
    }

    @Override
    public void onBindViewHolder(DataViewHolder<Pair<H, Boolean>> holder, int position) {
        holder.populate(new Pair<>(items.get(position), isChecked(position)));
    }

    public Iterable<Pair<H, Boolean>> getItemsAndState() {
        return () -> {
            return new Iterator<Pair<H, Boolean>>() {

                private int current = 0;

                @Override
                public boolean hasNext() {
                    return current < items.size();
                }

                @Override
                public Pair<H, Boolean> next() {
                    Pair<H, Boolean> value = new Pair<>(items.get(current), isChecked(current));
                    current++;
                    return value;
                }
            };
        };
    }

    /**
     * Set items and their state. Similar to {@link #setItems(List)}, but with a custom state for every item. This can
     * be useful when populating the list if the previous states were saved.
     * <p>
     * If the state of every item is the same, it is more efficient to use {@link #setItems(List, boolean)}.
     *
     * @param values
     */
    public void setItemsAndState(List<Pair<H, Boolean>> values) {
        List<H> items = new ArrayList<>();
        booleanArray.clear();
        for (int i = 0; i < values.size(); i++) {
            Pair<H, Boolean> value = values.get(i);
            items.add(value.first);
            if (value.second != getDefaultValue()) {
                booleanArray.append(i, value.second);
            }
        }
        setItems(items);
    }
}