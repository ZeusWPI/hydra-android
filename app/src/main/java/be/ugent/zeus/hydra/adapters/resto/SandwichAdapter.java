package be.ugent.zeus.hydra.adapters.resto;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.resto.Sandwich;
import com.kyo.expandablelayout.ExpandableLayout;

import java.util.Collections;
import java.util.List;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Adapter for sandwiches.
 *
 * @author Niko Strijbol
 */
public class SandwichAdapter extends RecyclerView.Adapter<SandwichAdapter.SandwichHolder> {

    private Context context;

    public static class SandwichHolder extends RecyclerView.ViewHolder {

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

    private List<Sandwich> data = Collections.emptyList();
    private SparseBooleanArray expanded = new SparseBooleanArray();

    public SandwichAdapter(Context context) {
        this.context = context;
    }

    /**
     * @param data Replace the current data.
     */
    public void replaceData(List<Sandwich> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public SandwichHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        //Inflate the layout
        View sandwichView = inflater.inflate(R.layout.item_sandwich, parent, false);

        return new SandwichHolder(sandwichView);
    }

    @Override
    public void onBindViewHolder(final SandwichHolder holder, final int position) {
        //Get sandwich from data.
        Sandwich sandwich = data.get(position);

        Resources r = context.getResources();

        //Set the data.
        holder.name.setText(sandwich.name);
        holder.mediumPrice.setText(String.format(r.getString(R.string.sandwich_price_medium), sandwich.price_medium));
        holder.smallPrice.setText(String.format(r.getString(R.string.sandwich_price_small), sandwich.price_small));
        String ingredients = TextUtils.join(", ", sandwich.ingredients);
        holder.ingredients.setText(String.format(r.getString(R.string.sandwich_ingredients), ingredients));
        holder.expandableLayout.setExpanded(expanded.get(position));
        holder.expandableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.expandableLayout.isExpanded()) {
                    expanded.delete(position);
                } else {
                    expanded.put(position, true);
                }
                holder.expandableLayout.toggleExpansion();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}