package be.ugent.zeus.hydra.ui.main.minerva;

import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.minerva.Announcement;
import be.ugent.zeus.hydra.ui.common.ViewUtils;
import be.ugent.zeus.hydra.ui.common.recyclerview.ResultStarter;
import be.ugent.zeus.hydra.ui.common.recyclerview.adapters.EmptyItemAdapter;

/**
 * Adapteer for announcements.
 *
 * @author Niko Strijbol
 */
class AnnouncementsAdapter extends EmptyItemAdapter<Announcement, AnnouncementsViewHolder> {

    private final ResultStarter starter;

    AnnouncementsAdapter(ResultStarter starter) {
        super(R.layout.item_no_data, null);
        this.starter = starter;
    }

    @Override
    protected AnnouncementsViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new AnnouncementsViewHolder(ViewUtils.inflate(parent, R.layout.item_minerva_extended_announcement), starter);
    }
}