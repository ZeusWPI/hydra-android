package be.ugent.zeus.hydra.recyclerview.adapters.sko;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.sko.Artist;
import be.ugent.zeus.hydra.recyclerview.adapters.common.ItemAdapter;
import be.ugent.zeus.hydra.recyclerview.viewholder.sko.LineupViewHolder;

/**
 * @author Niko Strijbol
 */
public class LineupAdapter extends ItemAdapter<Artist, LineupViewHolder> {

    @Override
    public LineupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sko_lineup, parent, false);
        return new LineupViewHolder(v);
    }
}