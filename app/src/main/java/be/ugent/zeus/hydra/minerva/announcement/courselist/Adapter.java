package be.ugent.zeus.hydra.minerva.announcement.courselist;

import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.minerva.announcement.Announcement;
import be.ugent.zeus.hydra.ui.common.ViewUtils;
import be.ugent.zeus.hydra.ui.common.recyclerview.ResultStarter;
import be.ugent.zeus.hydra.ui.common.recyclerview.adapters.ItemDiffAdapter;

/**
 * Adapter for announcements.
 *
 * @author Niko Strijbol
 */
class Adapter extends ItemDiffAdapter<Announcement, ViewHolder> {

    private final ResultStarter starter;

    Adapter(ResultStarter starter) {
        this.starter = starter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ViewUtils.inflate(parent, R.layout.item_minerva_announcement), starter);
    }
}