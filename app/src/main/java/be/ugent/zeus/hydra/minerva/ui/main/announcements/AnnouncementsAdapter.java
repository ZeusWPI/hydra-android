package be.ugent.zeus.hydra.minerva.ui.main.announcements;

import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.minerva.Announcement;
import be.ugent.zeus.hydra.ui.common.ViewUtils;
import be.ugent.zeus.hydra.ui.common.recyclerview.ResultStarter;
import be.ugent.zeus.hydra.ui.common.recyclerview.adapters.MultiSelectDiffAdapter;

/**
 * Adapteer for announcements.
 *
 * @author Niko Strijbol
 */
class AnnouncementsAdapter extends MultiSelectDiffAdapter<Announcement> {

    private final ResultStarter starter;

    AnnouncementsAdapter(ResultStarter starter) {
        this.starter = starter;
    }

    @Override
    public AnnouncementsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AnnouncementsViewHolder(ViewUtils.inflate(parent, R.layout.item_minerva_extended_announcement), starter, this);
    }
}