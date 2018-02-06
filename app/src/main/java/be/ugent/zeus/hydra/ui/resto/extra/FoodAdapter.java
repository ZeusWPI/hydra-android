package be.ugent.zeus.hydra.ui.resto.extra;

import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.ViewUtils;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.ItemDiffAdapter;
import be.ugent.zeus.hydra.resto.Food;

/**
 * Adapter for food items.
 *
 * @author Niko Strijbol
 */
public class FoodAdapter extends ItemDiffAdapter<Food, FoodViewHolder> {

    @Override
    public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FoodViewHolder(ViewUtils.inflate(parent, R.layout.item_resto_fooditem));
    }
}