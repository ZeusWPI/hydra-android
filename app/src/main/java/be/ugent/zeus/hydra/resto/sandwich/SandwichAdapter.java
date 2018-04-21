package be.ugent.zeus.hydra.resto.sandwich;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.ViewUtils;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.MultiSelectAdapter;

/**
 * Adapter for sandwiches.
 *
 * @author Niko Strijbol
 */
class SandwichAdapter extends MultiSelectAdapter<Sandwich> {

    @NonNull
    @Override
    public SandwichHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SandwichHolder(ViewUtils.inflate(parent, R.layout.item_sandwich), this);
    }
}