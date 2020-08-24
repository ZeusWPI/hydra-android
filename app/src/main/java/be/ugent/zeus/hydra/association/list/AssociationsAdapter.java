package be.ugent.zeus.hydra.association.list;

import android.util.Pair;
import android.view.ViewGroup;
import androidx.annotation.NonNull;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.MultiSelectAdapter;
import be.ugent.zeus.hydra.common.utils.ViewUtils;

/**
 * @author Niko Strijbol
 */
class AssociationsAdapter extends MultiSelectAdapter<Association> {

    @NonNull
    @Override
    public AssociationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AssociationViewHolder(ViewUtils.inflate(parent, R.layout.item_checkbox_string), this);
    }

}
