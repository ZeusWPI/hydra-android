package be.ugent.zeus.hydra.association.list;

import android.view.ViewGroup;
import androidx.annotation.NonNull;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.DiffAdapter;
import be.ugent.zeus.hydra.common.utils.ViewUtils;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.SearchableAdapter;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.common.utils.ViewUtils;

/**
 * Adapter for the list of activities.
 *
 * @author ellen
 * @author Niko Strijbol
 */
class EventAdapter extends DiffAdapter<EventItem, DataViewHolder<EventItem>> {

    private static final int HEADER_TYPE = 25;

    @NonNull
    @Override
    public DataViewHolder<EventItem> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEADER_TYPE) {
            return new DateHeaderViewHolder(ViewUtils.inflate(parent, R.layout.item_event_date_header));
        } else {
            return new EventViewHolder(ViewUtils.inflate(parent, R.layout.item_event_item));
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
