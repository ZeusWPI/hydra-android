package be.ugent.zeus.hydra.ui.main.events;

import android.text.TextUtils;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.association.Association;
import be.ugent.zeus.hydra.data.models.association.Event;
import be.ugent.zeus.hydra.ui.common.ViewUtils;
import be.ugent.zeus.hydra.ui.common.recyclerview.adapters.SearchableDiffAdapter;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DateHeaderViewHolder;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

/**
 * Adapter for the list of activities.
 *
 * @author ellen
 * @author Niko Strijbol
 */
class EventAdapter extends SearchableDiffAdapter<Event, EventViewHolder> implements StickyRecyclerHeadersAdapter<DateHeaderViewHolder> {

    protected EventAdapter() {
        super((event, s) -> {
            if (!TextUtils.isEmpty(event.getTitle()) && event.getTitle().toLowerCase().contains(s)) {
                return true;
            }
            if (event.getAssociation() != null) {
                Association association = event.getAssociation();
                if (!TextUtils.isEmpty(association.getDisplayName()) && association.getDisplayName().toLowerCase().contains(s)) {
                    return true;
                }
                if (!TextUtils.isEmpty(association.getFullName()) && association.getFullName().toLowerCase().contains(s)) {
                    return true;
                }
                if (association.getInternalName().contains(s)) {
                    return true;
                }
            }
            return false;
        });
    }

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