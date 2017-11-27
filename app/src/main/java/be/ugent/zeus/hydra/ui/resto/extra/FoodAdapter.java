package be.ugent.zeus.hydra.ui.resto.extra;

import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.domain.models.resto.Food;
import be.ugent.zeus.hydra.ui.common.ViewUtils;
import be.ugent.zeus.hydra.ui.common.recyclerview.adapters.ItemDiffAdapter;

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