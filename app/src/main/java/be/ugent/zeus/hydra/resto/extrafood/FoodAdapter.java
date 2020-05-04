package be.ugent.zeus.hydra.resto.extrafood;

import androidx.annotation.NonNull;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.utils.ViewUtils;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.DiffAdapter;

/**
 * Adapter for food items.
 *
 * @author Niko Strijbol
 */
public class FoodAdapter extends DiffAdapter<Food, FoodViewHolder> {

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FoodViewHolder(ViewUtils.inflate(parent, R.layout.item_resto_fooditem));
    }
}
