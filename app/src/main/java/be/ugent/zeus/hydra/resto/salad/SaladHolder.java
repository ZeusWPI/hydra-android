package be.ugent.zeus.hydra.resto.salad;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.MultiSelectAdapter;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import net.cachapa.expandablelayout.ExpandableLayout;

/**
 * @author Niko Strijbol
 */
class SaladHolder extends DataViewHolder<SaladBowl> {

    private final TextView name;
    private final TextView price;
    private final ExpandableLayout expandableLayout;
    private final TextView description;
    private final MultiSelectAdapter<SaladBowl> adapter;

    SaladHolder(View itemView, MultiSelectAdapter<SaladBowl> adapter) {
        super(itemView);

        name = itemView.findViewById(R.id.name);
        price = itemView.findViewById(R.id.price);
        expandableLayout = itemView.findViewById(R.id.expandable_layout);
        description = itemView.findViewById(R.id.description);
        this.adapter = adapter;
    }

    @Override
    public void populate(SaladBowl bowl) {
        Context c = itemView.getContext();
        name.setText(bowl.getName());
        price.setText(String.format(c.getString(R.string.resto_salad_price), bowl.getPrice()));
        description.setText(bowl.getDescription());
        expandableLayout.setExpanded(adapter.isChecked(getAdapterPosition()), false);
        itemView.setOnClickListener(v -> {
            adapter.setChecked(getAdapterPosition());
            expandableLayout.toggle();
        });
    }
}
