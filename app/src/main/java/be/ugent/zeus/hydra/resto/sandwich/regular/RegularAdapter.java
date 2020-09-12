package be.ugent.zeus.hydra.resto.sandwich.regular;

import android.view.ViewGroup;
import androidx.annotation.NonNull;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.MultiSelectAdapter;
import be.ugent.zeus.hydra.common.utils.ViewUtils;

/**
 * Adapter for sandwiches.
 *
 * @author Niko Strijbol
 */
class RegularAdapter extends MultiSelectAdapter<RegularSandwich> {

    @NonNull
    @Override
    public RegularHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RegularHolder(ViewUtils.inflate(parent, R.layout.item_sandwich), this);
    }
}
