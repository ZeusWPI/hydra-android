package be.ugent.zeus.hydra.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.info.InfoItem;
import be.ugent.zeus.hydra.recyclerviewholder.InfoListViewHolder;

import java.util.ArrayList;

/**
 * Created by ellen on 8/3/16.
 */
public class InfoListAdapter extends RecyclerView.Adapter<InfoListViewHolder> {

    private ArrayList<InfoItem> items;

    public InfoListAdapter() {
        this.items = new ArrayList<>();
    }

    @Override
    public InfoListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_card, parent, false);
        return new InfoListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(InfoListViewHolder holder, int position) {
        final InfoItem category = items.get(position);
        holder.populate(category);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(ArrayList<InfoItem> items) {
        this.items.clear();
        for (InfoItem item : items) {
            this.items.add(item);
        }
    }
}