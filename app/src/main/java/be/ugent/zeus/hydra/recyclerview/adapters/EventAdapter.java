package be.ugent.zeus.hydra.recyclerview.adapters;

import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.association.Event;
import be.ugent.zeus.hydra.recyclerview.adapters.common.ItemAdapter;
import be.ugent.zeus.hydra.recyclerview.viewholder.DateHeaderViewHolder;
import be.ugent.zeus.hydra.recyclerview.viewholder.EventViewHolder;
import be.ugent.zeus.hydra.utils.ViewUtils;
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
        return new EventViewHolder(ViewUtils.inflate(parent, R.layout.item_activity));
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
        return new DateHeaderViewHolder(ViewUtils.inflate(parent, R.layout.item_date_header));
    }

    @Override
    public void onBindHeaderViewHolder(DateHeaderViewHolder holder, int position) {
        holder.populate(items.get(position).getStart());
    }
}