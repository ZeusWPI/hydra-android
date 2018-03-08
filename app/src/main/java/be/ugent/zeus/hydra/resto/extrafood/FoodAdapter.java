package be.ugent.zeus.hydra.resto.extrafood;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.ViewUtils;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.ItemAdapter2;

/**
 * Adapter for food items.
 *
 * @author Niko Strijbol
 */
public class FoodAdapter extends ItemAdapter2<Food, FoodViewHolder> {

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FoodViewHolder(ViewUtils.inflate(parent, R.layout.item_resto_fooditem));
    }
}