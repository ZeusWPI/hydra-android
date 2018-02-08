package be.ugent.zeus.hydra.info.list;

import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.ViewUtils;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.ItemDiffAdapter;
import be.ugent.zeus.hydra.info.InfoItem;

/**
 * Adapter for the list of information items.
 *
 * @author Niko Strijbol
 * @author Ellen
 */
class InfoListAdapter extends ItemDiffAdapter<InfoItem, InfoViewHolder> {

    InfoListAdapter() {
        super();
    }

    @Override
    public InfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new InfoViewHolder(ViewUtils.inflate(parent, R.layout.info_card));
    }
}