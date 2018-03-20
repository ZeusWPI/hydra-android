package be.ugent.zeus.hydra.info;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.ViewUtils;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.ItemAdapter2;

/**
 * Adapter for the list of information items.
 *
 * @author Niko Strijbol
 * @author Ellen
 */
class InfoListAdapter extends ItemAdapter2<InfoItem, InfoViewHolder> {

    InfoListAdapter() {
        super();
    }

    @NonNull
    @Override
    public InfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InfoViewHolder(ViewUtils.inflate(parent, R.layout.info_card));
    }
}