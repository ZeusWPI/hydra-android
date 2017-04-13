package be.ugent.zeus.hydra.ui.main;

import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.association.Event;
import be.ugent.zeus.hydra.ui.common.recyclerview.DateHeaderViewHolder;
import be.ugent.zeus.hydra.ui.common.recyclerview.ItemAdapter;
import be.ugent.zeus.hydra.ui.common.ViewUtils;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

/**
 * Adapter for the list of activities.
 *
 * @author ellen
 * @author Niko Strijbol
 */
class EventAdapter extends ItemAdapter<Event, EventViewHolder> implements StickyRecyclerHeadersAdapter<DateHeaderViewHolder> {

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