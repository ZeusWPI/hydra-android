package be.ugent.zeus.hydra.recyclerview.adapters.minerva;

import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.minerva.Announcement;
import be.ugent.zeus.hydra.recyclerview.adapters.common.EmptyItemLoader;
import be.ugent.zeus.hydra.recyclerview.viewholder.minerva.AnnouncementViewHolder;
import be.ugent.zeus.hydra.utils.ViewUtils;

/**
 * Adapteer for announcements.
 *
 * @author Niko Strijbol
 */
public class AnnouncementAdapter extends EmptyItemLoader<Announcement, AnnouncementViewHolder> {

    public AnnouncementAdapter() {
        super(R.layout.item_no_data);
    }

    @Override
    protected AnnouncementViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new AnnouncementViewHolder(ViewUtils.inflate(parent, R.layout.item_minerva_announcement));
    }

    public void remove(int position) {
        this.items.remove(position);
        notifyItemRemoved(position);
    }

    public void add(Announcement announcement) {
        //Check for place holder
        boolean empty = this.items.isEmpty();
        this.items.add(0, announcement);
        if (empty) {
            notifyItemRemoved(0);
        }
        notifyItemInserted(0);
    }

    public Announcement get(int position) {
        return items.get(position);
    }
}