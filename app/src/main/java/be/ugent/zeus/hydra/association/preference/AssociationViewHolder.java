package be.ugent.zeus.hydra.association.preference;


import android.util.Pair;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;

/**
 * Simple view holder.
 *
 * Needs to be static because generics are stupid.
 */
class AssociationViewHolder extends DataViewHolder<Pair<Association, Boolean>> {

    private final CheckBox checkBox;
    private final LinearLayout parent;
    private final TextView title;

    private final SearchableAssociationsAdapter adapter;

    AssociationViewHolder(View itemView, SearchableAssociationsAdapter adapter) {

        super(itemView);
        this.adapter = adapter;

        this.checkBox = itemView.findViewById(R.id.checkbox);
        this.parent = itemView.findViewById(R.id.parent_layout);
        this.title = itemView.findViewById(R.id.title_checkbox);
    }

    @Override
    public void populate(Pair<Association, Boolean> data) {
        title.setText(data.first.getName());
        checkBox.setChecked(data.second);
        parent.setOnClickListener(v -> {
            adapter.setChecked(getAdapterPosition());
            checkBox.toggle();
        });
        checkBox.setOnClickListener(v -> adapter.setChecked(getAdapterPosition()));
    }
}
