package be.ugent.zeus.hydra.minerva.announcement.unreadlist;

import androidx.annotation.NonNull;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.ViewUtils;
import be.ugent.zeus.hydra.common.ui.recyclerview.ResultStarter;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.MultiSelectAdapter;
import be.ugent.zeus.hydra.minerva.announcement.Announcement;

/**
 * Adapteer for announcements.
 *
 * @author Niko Strijbol
 */
class AnnouncementsAdapter extends MultiSelectAdapter<Announcement> {

    private final ResultStarter starter;

    AnnouncementsAdapter(ResultStarter starter) {
        this.starter = starter;
    }

    @NonNull
    @Override
    public AnnouncementsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AnnouncementsViewHolder(ViewUtils.inflate(parent, R.layout.item_minerva_extended_announcement), starter, this);
    }
}