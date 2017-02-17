package be.ugent.zeus.hydra.recyclerview.adapters.sko;

import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.sko.Artist;
import be.ugent.zeus.hydra.recyclerview.adapters.common.ItemAdapter;
import be.ugent.zeus.hydra.recyclerview.viewholder.sko.LineupViewHolder;
import be.ugent.zeus.hydra.utils.ViewUtils;

/**
 * @author Niko Strijbol
 */
public class LineupAdapter extends ItemAdapter<Artist, LineupViewHolder> {

    @Override
    public LineupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LineupViewHolder(ViewUtils.inflate(parent, R.layout.item_sko_lineup));
    }
}