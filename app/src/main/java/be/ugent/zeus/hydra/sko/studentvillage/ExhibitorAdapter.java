package be.ugent.zeus.hydra.sko.studentvillage;

import androidx.annotation.NonNull;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.ViewUtils;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.DiffAdapter;

/**
 * Exhibitors. Can be filtered on the name of the exhibitor.
 *
 * @author Niko Strijbol
 */
class ExhibitorAdapter extends DiffAdapter<Exhibitor, ExhibitorViewHolder> {

    @NonNull
    @Override
    public ExhibitorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExhibitorViewHolder(ViewUtils.inflate(parent, R.layout.item_sko_exhibitor));
    }
}