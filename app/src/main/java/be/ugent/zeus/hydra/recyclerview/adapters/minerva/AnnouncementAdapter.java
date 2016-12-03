package be.ugent.zeus.hydra.recyclerview.adapters.minerva;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.minerva.Announcement;
import be.ugent.zeus.hydra.recyclerview.adapters.common.EmptyItemLoader;
import be.ugent.zeus.hydra.recyclerview.viewholder.minerva.AnnouncementViewHolder;

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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_minerva_announcement, parent, false);
        return new AnnouncementViewHolder(v);
    }

    public void remove(int position) {
        this.items.remove(position);
        notifyItemRemoved(position);
    }

    public void add(Announcement announcement) {
        //Check for place holder
        boolean empty = this.items.isEmpty();
        this.items.add(0, announcement);
        if(empty) {
            notifyItemRemoved(0);
        }
        notifyItemInserted(0);
    }

    public Announcement get(int position) {
        return items.get(position);
    }
}