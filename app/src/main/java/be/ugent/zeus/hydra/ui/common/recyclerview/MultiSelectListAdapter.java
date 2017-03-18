package be.ugent.zeus.hydra.ui.common.recyclerview;

import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.utils.ViewUtils;
import java8.util.function.Function;

import java.util.ArrayList;
import java.util.List;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Adapter with items that are checkable.
 *
 * @author Niko Strijbol
 */
public class MultiSelectListAdapter<H> extends ItemAdapter<Pair<H, Boolean>, MultiSelectListAdapter.ViewHolder<H>> {

    protected Function<H, String> displayNameProvider = Object::toString;

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
     * Set the provider that converts the element to a display value.
     *
     * @param provider The provider.
     */
    public void setDisplayNameProvider(Function<H, String> provider) {
        displayNameProvider = provider;
        notifyDataSetChanged();
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
    public ViewHolder<H> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder<>(ViewUtils.inflate(parent, R.layout.item_checkbox_string), this);
    }

    /**
     * Simple view holder.
     *
     * Needs to be static because generics are stupid.
     */
    protected static class ViewHolder<E> extends DataViewHolder<Pair<E, Boolean>> {

        private CheckBox checkBox;
        private LinearLayout parent;
        private MultiSelectListAdapter<E> adapter;
        private TextView title;

        /**
         * The constructor is the place to bind views and other non-data related stuff.
         *
         * @param itemView The parent view.
         */
        public ViewHolder(View itemView, MultiSelectListAdapter<E> adapter) {
            super(itemView);
            this.adapter = adapter;
            checkBox = $(itemView, R.id.checkbox);
            parent = $(itemView, R.id.parent_layout);
            title = $(itemView, R.id.title_checkbox);
        }

        @Override
        public void populate(Pair<E, Boolean> data) {
            title.setText(adapter.displayNameProvider.apply(data.first));
            checkBox.setChecked(data.second);
            parent.setOnClickListener(v -> {
                adapter.setChecked(getAdapterPosition());
                checkBox.toggle();
            });
            checkBox.setOnClickListener(v -> adapter.setChecked(getAdapterPosition()));
        }
    }
}