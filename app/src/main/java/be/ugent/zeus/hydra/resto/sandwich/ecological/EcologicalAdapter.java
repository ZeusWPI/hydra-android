package be.ugent.zeus.hydra.resto.sandwich.ecological;

import android.view.ViewGroup;
import androidx.annotation.NonNull;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.ViewUtils;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.MultiSelectAdapter;

/**
 * Adapter for sandwiches.
 *
 * @author Niko Strijbol
 */
class EcologicalAdapter extends MultiSelectAdapter<EcologicalSandwich> {

    @NonNull
    @Override
    public EcologicalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EcologicalViewHolder(ViewUtils.inflate(parent, R.layout.item_sandwich_eco), this);
    }
}
