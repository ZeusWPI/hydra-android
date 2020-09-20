package be.ugent.zeus.hydra.resto.salad;

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
class SaladAdapter extends MultiSelectAdapter<SaladBowl> {

    @NonNull
    @Override
    public SaladHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SaladHolder(ViewUtils.inflate(parent, R.layout.item_salad_bowl), this);
    }
}
