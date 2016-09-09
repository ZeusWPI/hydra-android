package be.ugent.zeus.hydra.recyclerview.adapters.minerva;

import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
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

    private Fragment fragment;

    public AnnouncementAdapter(@LayoutRes int emptyViewId) {
        super(emptyViewId);
    }

    public AnnouncementAdapter(@LayoutRes int emptyViewId, Fragment fragment) {
        super(emptyViewId);
        this.fragment = fragment;
    }

    @Override
    protected AnnouncementViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_minerva_announcement, parent, false);
        return new AnnouncementViewHolder(v, fragment);
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

    public int positionOf(int id) {
        for (int i = 0; i < items.size(); i++) {
            if(items.get(i).getItemId() == id) {
                return i;
            }
        }

        return -1;
    }
}