package be.ugent.zeus.hydra.ui.resto;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.resto.Sandwich;
import be.ugent.zeus.hydra.ui.common.ViewUtils;
import be.ugent.zeus.hydra.ui.common.recyclerview.adapters.Adapter;
import net.cachapa.expandablelayout.ExpandableLayout;

import static be.ugent.zeus.hydra.ui.common.ViewUtils.$;

/**
 * Adapter for sandwiches.
 *
 * @author Niko Strijbol
 */
public class SandwichAdapter extends Adapter<Sandwich, SandwichAdapter.SandwichHolder> {

    static class SandwichHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView smallPrice;
        private TextView mediumPrice;
        private ExpandableLayout expandableLayout;
        private TextView ingredients;

        private SandwichHolder(View itemView) {
            super(itemView);

            name = $(itemView, R.id.sandwich_name);
            mediumPrice = $(itemView, R.id.sandwich_price_medium);
            smallPrice = $(itemView, R.id.sandwich_price_small);
            expandableLayout = $(itemView, R.id.expandable_layout);
            ingredients = $(itemView, R.id.sandwich_ingredients);
        }
    }

    private SparseBooleanArray expanded = new SparseBooleanArray();

    @Override
    public SandwichHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SandwichHolder(ViewUtils.inflate(parent, R.layout.item_sandwich));
    }

    @Override
    public void onBindViewHolder(final SandwichHolder holder, final int position) {
        //Get sandwich from data.
        Sandwich sandwich = items.get(position);

        Context c = holder.itemView.getContext();

        //Set the data.
        holder.name.setText(sandwich.name);
        holder.mediumPrice.setText(String.format(c.getString(R.string.sandwich_price_medium), sandwich.getPriceMedium()));
        holder.smallPrice.setText(String.format(c.getString(R.string.sandwich_price_small), sandwich.getPriceSmall()));
        String ingredients = TextUtils.join(", ", sandwich.getIngredients());
        holder.ingredients.setText(String.format(c.getString(R.string.sandwich_ingredients), ingredients));

        holder.expandableLayout.setExpanded(expanded.get(position), false);

        holder.itemView.setOnClickListener(v -> {
            expanded.put(holder.getAdapterPosition(), !holder.expandableLayout.isExpanded());
            holder.expandableLayout.toggle();
        });
    }
}