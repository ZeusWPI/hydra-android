package be.ugent.zeus.hydra.ui.main.events;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.association.Event;
import be.ugent.zeus.hydra.ui.EventDetailActivity;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;
import org.threeten.bp.format.DateTimeFormatter;

/**
 * View holder for an event in the event tab.
 *
 * @author Niko Strijbol
 */
class EventViewHolder extends DataViewHolder<EventItem> {

    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private TextView start;
    private TextView title;
    private TextView association;
    private CardView cardView;
    private View divider;

    EventViewHolder(View v) {
        super(v);
        title = v.findViewById(R.id.name);
        association = v.findViewById(R.id.association);
        start = v.findViewById(R.id.starttime);
        cardView = v.findViewById(R.id.card_view);
        divider = v.findViewById(R.id.item_event_divider);
    }

    public void populate(final EventItem eventItem) {
        Event event = eventItem.getItem();
        title.setText(event.getTitle());
        association.setText(event.getAssociation().getDisplayName());
        start.setText(event.getLocalStart().format(HOUR_FORMATTER));
        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EventDetailActivity.class);
            intent.putExtra(EventDetailActivity.PARCEL_EVENT, (Parcelable) event);
            v.getContext().startActivity(intent);
        });

        // Set the corners and bottom edge.
        // Hide the divider in the last case.
        divider.setVisibility(eventItem.isLastOfSection() ? View.GONE : View.VISIBLE);
    }
}