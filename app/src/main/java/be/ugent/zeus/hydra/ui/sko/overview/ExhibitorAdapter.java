package be.ugent.zeus.hydra.ui.sko.overview;

import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.sko.Exhibitor;
import be.ugent.zeus.hydra.ui.common.ViewUtils;
import be.ugent.zeus.hydra.ui.common.recyclerview.adapters.ItemDiffAdapter;

/**
 * Exhibitors. Can be filtered on the name of the exhibitor.
 *
 * @author Niko Strijbol
 */
public class ExhibitorAdapter extends ItemDiffAdapter<Exhibitor, ExhibitorViewHolder> {

    @Override
    public ExhibitorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ExhibitorViewHolder(ViewUtils.inflate(parent, R.layout.item_sko_exhibitor));
    }
}