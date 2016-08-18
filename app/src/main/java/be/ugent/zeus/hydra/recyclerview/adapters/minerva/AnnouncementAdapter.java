package be.ugent.zeus.hydra.recyclerview.adapters.minerva;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.minerva.Announcement;
import be.ugent.zeus.hydra.recyclerview.adapters.ItemAdapter;
import be.ugent.zeus.hydra.recyclerview.viewholder.minerva.AnnouncementViewHolder;

/**
 * Adapteer for announcements.
 *
 * @author Niko Strijbol
 */
public class AnnouncementAdapter extends ItemAdapter<Announcement, AnnouncementViewHolder> {

    @Override
    public AnnouncementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AnnouncementViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_minerva_announcement, parent, false));
    }

    @Override
    public void onBindViewHolder(AnnouncementViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
    }
}