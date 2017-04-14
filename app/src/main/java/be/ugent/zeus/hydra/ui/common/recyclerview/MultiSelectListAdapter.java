package be.ugent.zeus.hydra.ui.common.recyclerview;

import android.support.annotation.LayoutRes;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.ui.common.ViewUtils;
import java8.util.function.Function;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter with items that are checkable.
 *
 * @author Niko Strijbol
 */
public class MultiSelectListAdapter<H>
        extends ItemAdapter<Pair<H, Boolean>, DataViewHolder<Pair<H, Boolean>>> {

    private DataViewHolderFactory<Pair<H, Boolean>> dataViewHolderFactory;

    protected final @LayoutRes int resource;

    public MultiSelectListAdapter(int resource) {
        this.resource = resource;
    }

    public void setDataViewHolderFactory(DataViewHolderFactory<Pair<H, Boolean>> dataViewHolderFactory){
        this.dataViewHolderFactory = dataViewHolderFactory;
    }

    /**
     * Set the values to use.
     *
     * @param values The values.
     * @param initial The initial value.
     */
    public void setValues(List<H> values, boolean initial) {

        List<Pair<H, Boolean>> list = new ArrayList<>();

        for (H element: values) {
            list.add(new Pair<>(element, initial));
        }

        this.setItems(list);
    }

    /**
     * Change the boolean value at the given position to the reverse value. This operation DOES NOT trigger a change
     * in the recycler view data, so the methods for changed items is not called. The rationale behind this is that
     * this method is meant to be attached to a checkbox as a listener. Then the checkbox is already updated.
     *
     * Implementation note: the value at the position is replaced with a new instance.
     *
     * @param position The position.
     */
    public void setChecked(int position) {
        Pair<H, Boolean> old = items.get(position);
        items.set(position, new Pair<>(old.first, !old.second));
    }

    /**
     * Set all items to the given boolean. This operation DOES trigger a layout update.
     *
     * Implementation note: if a value is wrong, it is replaced with a new instance. If it is the right value, it is
     * left alone.
     *
     * @param checked The value.
     */
    public void setAllChecked(boolean checked) {

        for (int i = 0; i < getItemCount(); i++){
            Pair<H, Boolean> item = items.get(i);
            if(item.second != checked) {
                H value = item.first;
                items.set(i, new Pair<>(value, checked));
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public DataViewHolder<Pair<H, Boolean>> onCreateViewHolder(ViewGroup parent, int viewType) {
        return dataViewHolderFactory.newInstance(ViewUtils.inflate(parent, resource));
    }
}