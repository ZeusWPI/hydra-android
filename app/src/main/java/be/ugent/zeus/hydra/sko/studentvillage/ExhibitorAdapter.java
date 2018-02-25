package be.ugent.zeus.hydra.sko.studentvillage;

import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.ViewUtils;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.ItemDiffAdapter;

/**
 * Exhibitors. Can be filtered on the name of the exhibitor.
 *
 * @author Niko Strijbol
 */
class ExhibitorAdapter extends ItemDiffAdapter<Exhibitor, ExhibitorViewHolder> {

    @Override
    public ExhibitorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ExhibitorViewHolder(ViewUtils.inflate(parent, R.layout.item_sko_exhibitor));
    }
}