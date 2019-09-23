package be.ugent.zeus.hydra.resto.sandwich.regular;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.MultiSelectAdapter;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import net.cachapa.expandablelayout.ExpandableLayout;

/**
 * @author Niko Strijbol
 */
class RegularHolder extends DataViewHolder<RegularSandwich> {

    private TextView name;
    private TextView smallPrice;
    private TextView mediumPrice;
    private ExpandableLayout expandableLayout;
    private TextView ingredients;
    private MultiSelectAdapter<RegularSandwich> adapter;

    RegularHolder(View itemView, MultiSelectAdapter<RegularSandwich> adapter) {
        super(itemView);

        name = itemView.findViewById(R.id.sandwich_name);
        mediumPrice = itemView.findViewById(R.id.sandwich_price_medium);
        smallPrice = itemView.findViewById(R.id.sandwich_price_small);
        expandableLayout = itemView.findViewById(R.id.expandable_layout);
        ingredients = itemView.findViewById(R.id.sandwich_ingredients);
        this.adapter = adapter;
    }

    private static final String TAG = "SandwichHolder";

    @Override
    public void populate(RegularSandwich sandwich) {
        Context c = itemView.getContext();
        name.setText(sandwich.getName());
        mediumPrice.setText(String.format(c.getString(R.string.resto_sandwich_price_medium), sandwich.getPriceMedium()));
        smallPrice.setText(String.format(c.getString(R.string.resto_sandwich_price_small), sandwich.getPriceSmall()));
        String ingredientsString = TextUtils.join(", ", sandwich.getIngredients());
        ingredients.setText(String.format(c.getString(R.string.resto_sandwich_ingredients), ingredientsString));
        expandableLayout.setExpanded(adapter.isChecked(getAdapterPosition()), false);
        itemView.setOnClickListener(v -> {
            adapter.setChecked(getAdapterPosition());
            expandableLayout.toggle();
        });
    }
}
