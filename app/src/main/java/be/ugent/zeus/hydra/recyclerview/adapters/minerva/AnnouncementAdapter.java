package be.ugent.zeus.hydra.recyclerview.adapters.minerva;

import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
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


    public AnnouncementAdapter(@LayoutRes int emptyViewId) {
        super(emptyViewId);
    }

    @Override
    protected AnnouncementViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new AnnouncementViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_minerva_announcement, parent, false));
    }
}