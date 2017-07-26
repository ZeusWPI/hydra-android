package be.ugent.zeus.hydra.ui.minerva.overview;

import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.minerva.Announcement;
import be.ugent.zeus.hydra.ui.common.ViewUtils;
import be.ugent.zeus.hydra.ui.common.recyclerview.ResultStarter;
import be.ugent.zeus.hydra.ui.common.recyclerview.adapters.DiffAdapter;

/**
 * Adapter for announcements.
 *
 * @author Niko Strijbol
 */
class AnnouncementAdapter extends DiffAdapter<Announcement, AnnouncementViewHolder> {

    private final ResultStarter starter;

    AnnouncementAdapter(ResultStarter starter) {
        this.starter = starter;
    }

    @Override
    public AnnouncementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AnnouncementViewHolder(ViewUtils.inflate(parent, R.layout.item_minerva_announcement), starter);
    }
}