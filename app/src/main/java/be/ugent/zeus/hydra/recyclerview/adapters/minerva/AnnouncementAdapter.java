package be.ugent.zeus.hydra.recyclerview.adapters.minerva;

import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.minerva.Announcement;
import be.ugent.zeus.hydra.recyclerview.adapters.common.EmptyItemAdapter;
import be.ugent.zeus.hydra.recyclerview.viewholder.minerva.AnnouncementViewHolder;
import be.ugent.zeus.hydra.utils.ViewUtils;

/**
 * Adapteer for announcements.
 *
 * @author Niko Strijbol
 */
public class AnnouncementAdapter extends EmptyItemAdapter<Announcement, AnnouncementViewHolder> {

    public AnnouncementAdapter() {
        super(R.layout.item_no_data);
    }

    @Override
    protected AnnouncementViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new AnnouncementViewHolder(ViewUtils.inflate(parent, R.layout.item_minerva_announcement));
    }
}