package be.ugent.zeus.hydra.ui.minerva.overview;

import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.minerva.Announcement;
import be.ugent.zeus.hydra.ui.common.recyclerview.EmptyItemAdapter;
import be.ugent.zeus.hydra.ui.common.ViewUtils;

/**
 * Adapteer for announcements.
 *
 * @author Niko Strijbol
 */
class AnnouncementAdapter extends EmptyItemAdapter<Announcement, AnnouncementViewHolder> {

    AnnouncementAdapter() {
        super(R.layout.item_no_data);
    }

    @Override
    protected AnnouncementViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new AnnouncementViewHolder(ViewUtils.inflate(parent, R.layout.item_minerva_announcement));
    }
}