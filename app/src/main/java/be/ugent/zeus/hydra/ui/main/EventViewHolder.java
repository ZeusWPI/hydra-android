package be.ugent.zeus.hydra.ui.main;

import android.content.Intent;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.EventDetailActivity;
import be.ugent.zeus.hydra.data.models.association.Event;
import be.ugent.zeus.hydra.ui.common.recyclerview.DataViewHolder;
import org.threeten.bp.format.DateTimeFormatter;

import static be.ugent.zeus.hydra.ui.common.ViewUtils.$;

/**
 * View holder for an event in the event tab.
 *
 * @author Niko Strijbol
 */
class EventViewHolder extends DataViewHolder<Event> {

    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private TextView start;
    private TextView title;
    private TextView association;

    EventViewHolder(View v) {
        super(v);
        title = $(v, R.id.name);
        association = $(v, R.id.association);
        start = $(v, R.id.starttime);
    }

    public void populate(final Event event) {
        title.setText(event.getTitle());
        association.setText(event.getAssociation().getDisplayName());
        start.setText(event.getLocalStart().format(HOUR_FORMATTER));
        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EventDetailActivity.class);
            intent.putExtra(EventDetailActivity.PARCEL_EVENT, (Parcelable) event);
            v.getContext().startActivity(intent);
        });
    }
}