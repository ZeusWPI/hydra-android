package be.ugent.zeus.hydra.association.event.list;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.ViewUtils;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.SearchableAdapter;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;

/**
 * Adapter for the list of activities.
 *
 * @author ellen
 * @author Niko Strijbol
 */
class EventAdapter extends SearchableAdapter<EventItem, DataViewHolder<EventItem>> {

    private final int HEADER_TYPE = 25;

    EventAdapter() {
        super(new EventSearchPredicate(), new EventSearchFilter());
    }

    @NonNull
    @Override
    public DataViewHolder<EventItem> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEADER_TYPE) {
            return new DateHeaderViewHolder(ViewUtils.inflate(parent, R.layout.item_events_date_header));
        } else {
            return new EventViewHolder(ViewUtils.inflate(parent, R.layout.item_activity));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).isHeader()) {
            return HEADER_TYPE;
        } else {
            return super.getItemViewType(position);
        }
    }
}