package be.ugent.zeus.hydra.ui.resto.extra;

import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.domain.models.resto.Food;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;

/**
 * @author Niko Strijbol
 */
class FoodViewHolder extends DataViewHolder<Food> {

    private TextView name;
    private TextView price;

    FoodViewHolder(View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.food_item_name);
        price = itemView.findViewById(R.id.food_item_price);
    }

    @Override
    public void populate(Food food) {
        name.setText(food.getName());
        price.setText(itemView.getContext().getString(R.string.resto_food_price, food.getPrice()));
    }
}
