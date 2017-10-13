package be.ugent.zeus.hydra.ui.main.events;

import android.content.Intent;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.association.Event;
import be.ugent.zeus.hydra.ui.EventDetailActivity;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;
import com.github.captain_miao.optroundcardview.OptRoundCardView;
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
    private OptRoundCardView cardView;
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

        // If this is the last event in it's section, we enable shadows and rounded corners.
        boolean isLast = eventItem.isLastOfSection();
        // Show the bottom left and right corner.
        cardView.showCorner(false, false, isLast, isLast);
        // Show the bottom show.
        cardView.showEdgeShadow(true, false, true, isLast);
        // Add some margin if there is a shadow. Otherwise the shadow is hidden. The margin is 4 DP, which together with
        // the 4 DP margin of the header of the next section results in the correct spacing of 8 DP between cards.
        // The RecyclerView also has a top and bottom padding of 4 DP (combined with clipToPadding=false) for the very first
        // and last element.
        int bottomMarginInPx = isLast ? itemView.getContext().getResources().getDimensionPixelSize(R.dimen.card_margin_half) : 0;
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
        params.bottomMargin = bottomMarginInPx;
        cardView.setLayoutParams(params);

        // Hide the divider in the last case.
        divider.setVisibility(eventItem.isLastOfSection() ? View.GONE : View.VISIBLE);
    }
}