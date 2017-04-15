package be.ugent.zeus.hydra.ui.common.recyclerview.viewholders;


import android.util.Pair;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.common.recyclerview.adapters.MultiSelectListAdapter;
import java8.util.function.Function;

import static be.ugent.zeus.hydra.ui.common.ViewUtils.$;

/**
 * Simple view holder.
 *
 * Needs to be static because generics are stupid.
 */
public class DefaultMultiSelectListViewHolder<E> extends DataViewHolder<Pair<E, Boolean>> {

    private final CheckBox checkBox;
    private final LinearLayout parent;
    private final TextView title;

    private final MultiSelectListAdapter<E> adapter;

    private final Function<E, String> displayNameProvider;

    /**
     * The constructor is the place to bind views and other non-data related stuff.
     *
     * @param itemView The parent view.
     */
    public DefaultMultiSelectListViewHolder(View itemView, MultiSelectListAdapter<E> adapter) {
        this(itemView, adapter, Object::toString);
    }

    public DefaultMultiSelectListViewHolder(View itemView, MultiSelectListAdapter<E> adapter, Function<E, String> displayNameProvider) {

        super(itemView);
        this.adapter = adapter;
        this.displayNameProvider = displayNameProvider;

        this.checkBox = $(itemView, R.id.checkbox);
        this.parent = $(itemView, R.id.parent_layout);
        this.title = $(itemView, R.id.title_checkbox);
    }

    @Override
    public void populate(Pair<E, Boolean> data) {
        title.setText(displayNameProvider.apply(data.first));
        checkBox.setChecked(data.second);
        parent.setOnClickListener(v -> {
            adapter.setChecked(getAdapterPosition());
            checkBox.toggle();
        });
        checkBox.setOnClickListener(v -> adapter.setChecked(getAdapterPosition()));
    }
}
