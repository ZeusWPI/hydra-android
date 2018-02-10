package be.ugent.zeus.hydra.sko.lineup;

import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.ViewUtils;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.ItemDiffAdapter;

/**
 * @author Niko Strijbol
 */
class LineupAdapter extends ItemDiffAdapter<Artist, LineupViewHolder> {

    LineupAdapter() {
        super();
    }

    @Override
    public LineupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LineupViewHolder(ViewUtils.inflate(parent, R.layout.item_sko_lineup));
    }
}