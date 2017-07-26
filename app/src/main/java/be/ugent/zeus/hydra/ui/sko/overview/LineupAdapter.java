package be.ugent.zeus.hydra.ui.sko.overview;

import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.sko.Artist;
import be.ugent.zeus.hydra.ui.common.ViewUtils;

/**
 * @author Niko Strijbol
 */
public class LineupAdapter extends be.ugent.zeus.hydra.ui.common.recyclerview.adapters.DiffAdapter<Artist, LineupViewHolder> {

    protected LineupAdapter() {
        super();
    }

    @Override
    public LineupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LineupViewHolder(ViewUtils.inflate(parent, R.layout.item_sko_lineup));
    }
}