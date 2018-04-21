package be.ugent.zeus.hydra.common.ui.recyclerview.viewholders;


import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.MultiSelectAdapter;
import java8.util.function.Function;

/*
 * ViewHolder for MultiSelectLists with both a title and description for each item.
 * (And of course a checkbox)
 */
public class DescriptionMultiSelectListViewHolder<E> extends DataViewHolder<E> {

    private final CheckBox checkBox;
    private final LinearLayout parent;
    private final TextView title;
    private final TextView description;

    private final MultiSelectAdapter<E> adapter;

    private final Function<E, String> titleProvider;
    private final Function<E, String> descriptionProvider;

    public DescriptionMultiSelectListViewHolder(
            View itemView,
            MultiSelectAdapter<E> adapter,
            Function<E, String> titleProvider,
            Function<E, String> descriptionProvider
    ) {
        super(itemView);
        this.adapter = adapter;
        this.titleProvider = titleProvider;
        this.descriptionProvider = descriptionProvider;

        this.checkBox = itemView.findViewById(R.id.checkbox);
        this.parent = itemView.findViewById(R.id.parent_layout);
        this.title = itemView.findViewById(R.id.title_checkbox);
        this.description = itemView.findViewById(R.id.description_checkbox);
    }


    @Override
    public void populate(E data) {
        title.setText(titleProvider.apply(data));
        description.setText(descriptionProvider.apply(data));
        checkBox.setChecked(adapter.isChecked(getAdapterPosition()));
        parent.setOnClickListener(v -> {
            adapter.setChecked(getAdapterPosition());
            checkBox.toggle();
        });
        checkBox.setOnClickListener(v -> adapter.setChecked(getAdapterPosition()));
    }
}