package be.ugent.zeus.hydra.recyclerview.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.association.Event;
import be.ugent.zeus.hydra.recyclerview.adapters.common.ItemAdapter;
import be.ugent.zeus.hydra.recyclerview.viewholder.EventViewHolder;
import be.ugent.zeus.hydra.recyclerview.viewholder.DateHeaderViewHolder;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

/**
 * Adapter for the list of activities.
 *
 * @author ellen
 * @author Niko Strijbol
 */
public class EventAdapter extends ItemAdapter<Event, EventViewHolder> implements StickyRecyclerHeadersAdapter<DateHeaderViewHolder> {

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activity, parent, false);
        return new EventViewHolder(v);
    }

    /**
     * We want to sort per day.
     */
    @Override
    public long getHeaderId(int position) {
        return items.get(position).getStart().toEpochSecond();
    }

    @Override
    public DateHeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_date_header, parent, false);
        return new DateHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(DateHeaderViewHolder holder, int position) {
        holder.populate(items.get(position).getStart());
    }
}