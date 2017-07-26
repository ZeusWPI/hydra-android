package be.ugent.zeus.hydra.ui.common.recyclerview.viewholders;


import android.util.Pair;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.common.recyclerview.adapters.MultiSelectDiffAdapter;
import java8.util.function.Function;

import static be.ugent.zeus.hydra.ui.common.ViewUtils.$;

/*
 * ViewHolder for MultiSelectLists with both a title and description for each item.
 * (And of course a checkbox)
 */
public class DescriptionMultiSelectListViewHolder<E> extends DataViewHolder<Pair<E, Boolean>> {

    private final CheckBox checkBox;
    private final LinearLayout parent;
    private final TextView title;
    private final TextView description;

    private final MultiSelectDiffAdapter<E> adapter;

    private final Function<E, String> titleProvider;
    private final Function<E, String> descriptionProvider;

    public DescriptionMultiSelectListViewHolder(
            View itemView,
            MultiSelectDiffAdapter<E> adapter,
            Function<E, String> titleProvider,
            Function<E, String> descriptionProvider
    ) {
        super(itemView);
        this.adapter = adapter;
        this.titleProvider = titleProvider;
        this.descriptionProvider = descriptionProvider;

        this.checkBox = $(itemView, R.id.checkbox);
        this.parent = $(itemView, R.id.parent_layout);
        this.title = $(itemView, R.id.title_checkbox);
        this.description = $(itemView, R.id.description_checkbox);
    }


    @Override
    public void populate(Pair<E, Boolean> data) {
        title.setText(titleProvider.apply(data.first));
        description.setText(descriptionProvider.apply(data.first));
        checkBox.setChecked(data.second);
        parent.setOnClickListener(v -> {
            adapter.setChecked(getAdapterPosition());
            checkBox.toggle();
        });
        checkBox.setOnClickListener(v -> adapter.setChecked(getAdapterPosition()));
    }
}