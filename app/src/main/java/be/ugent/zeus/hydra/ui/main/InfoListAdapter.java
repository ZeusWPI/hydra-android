package be.ugent.zeus.hydra.ui.main;

import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.info.InfoItem;
import be.ugent.zeus.hydra.ui.common.recyclerview.ItemAdapter;
import be.ugent.zeus.hydra.utils.ViewUtils;

/**
 * Adapter for the list of information items.
 *
 * @author Niko Strijbol
 * @author Ellen
 */
class InfoListAdapter extends ItemAdapter<InfoItem, InfoViewHolder> {

    @Override
    public InfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new InfoViewHolder(ViewUtils.inflate(parent, R.layout.info_card));
    }
}