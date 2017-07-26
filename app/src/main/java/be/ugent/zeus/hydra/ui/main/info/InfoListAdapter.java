package be.ugent.zeus.hydra.ui.main.info;

import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.info.InfoItem;
import be.ugent.zeus.hydra.ui.common.ViewUtils;

/**
 * Adapter for the list of information items.
 *
 * @author Niko Strijbol
 * @author Ellen
 */
class InfoListAdapter extends be.ugent.zeus.hydra.ui.common.recyclerview.adapters.DiffAdapter<InfoItem, InfoViewHolder> {

    protected InfoListAdapter() {
        super();
    }

    @Override
    public InfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new InfoViewHolder(ViewUtils.inflate(parent, R.layout.info_card));
    }
}