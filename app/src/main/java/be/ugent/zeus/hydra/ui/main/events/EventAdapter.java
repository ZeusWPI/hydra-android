package be.ugent.zeus.hydra.ui.main.events;

import android.text.TextUtils;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.association.Association;
import be.ugent.zeus.hydra.data.models.association.Event;
import be.ugent.zeus.hydra.ui.common.ViewUtils;
import be.ugent.zeus.hydra.ui.common.recyclerview.adapters.GenericSearchableAdapter;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

/**
 * Adapter for the list of activities.
 *
 * @author ellen
 * @author Niko Strijbol
 */
class EventAdapter extends GenericSearchableAdapter<EventItem, DataViewHolder<EventItem>, Event> {

    private final int HEADER_TYPE = 25;

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
                },
                eventItems -> StreamSupport.stream(eventItems)
                        .filter(EventItem::isItem)
                        .map(EventItem::getItem)
                        .collect(Collectors.toList()),
                new EventItem.Converter()
        );
    }

    @Override
    public DataViewHolder<EventItem> onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER_TYPE) {
            return new DateHeaderViewHolder(ViewUtils.inflate(parent, R.layout.item_events_date_header));
        } else {
            return new EventViewHolder(ViewUtils.inflate(parent, R.layout.item_activity));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position).isHeader()) {
            return HEADER_TYPE;
        } else {
            return super.getItemViewType(position);
        }
    }
}