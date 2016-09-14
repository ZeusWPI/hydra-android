package be.ugent.zeus.hydra.recyclerview.adapters;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.recyclerview.adapters.common.ItemAdapter;
import be.ugent.zeus.hydra.recyclerview.viewholder.DataViewHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Adapter with items that are checkable.
 *
 * @author Niko Strijbol
 */
public class MultiSelectListAdapter<H> extends ItemAdapter<Pair<H, Boolean>, MultiSelectListAdapter.ViewHolder<H>> {

    /**
     * Set the values to use.
     *
     * @param values The values.
     *
     * @param initial The initial value.
     */
    public void setValues(Collection<H> values, boolean initial) {

        List<Pair<H, Boolean>> list = new ArrayList<>();

        for (H element: values) {
            list.add(new Pair<>(element, initial));
        }

        this.setItems(list);
    }

    /**
     * Change the boolean value at the given position to the given value. This operation DOES NOT trigger a change
     * in the recycler view data, so the methods for changed items is not called. The rationale behind this is that
     * this method is meant to be attached to a checkbox as a listener. Then the checkbox is already updated.
     *
     * Implementation note: the value at the position is replaced with a new instance.
     *
     * @param position The position.
     * @param checked The value.
     */
    public void setChecked(int position, boolean checked) {
        H value = items.get(position).first;
        items.set(position, new Pair<>(value, checked));
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

        for (int i = 0; i < items.size(); i++){
            Pair<H, Boolean> item = items.get(0);
            if(item.second != checked) {
                H value = item.first;
                items.set(i, new Pair<>(value, checked));
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public ViewHolder<H> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder<>(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkbox_string, parent, false), this);
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

            title.setText(data.first.toString());
            checkBox.setChecked(data.second);

            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBox.toggle();
                }
            });

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    adapter.setChecked(getAdapterPosition(), isChecked);
                }
            });
        }
    }
}