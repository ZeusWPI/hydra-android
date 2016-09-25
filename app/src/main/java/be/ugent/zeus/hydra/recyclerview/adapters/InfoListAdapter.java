package be.ugent.zeus.hydra.recyclerview.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.info.InfoItem;
import be.ugent.zeus.hydra.recyclerview.adapters.common.ItemAdapter;
import be.ugent.zeus.hydra.recyclerview.viewholder.InfoViewHolder;

/**
 * Created by ellen on 8/3/16.
 */
public class InfoListAdapter extends ItemAdapter<InfoItem, InfoViewHolder> {

    @Override
    public InfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new InfoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.info_card, parent, false));
    }
}