package be.ugent.zeus.hydra.feed.cards.implementations.event;

import android.app.Activity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.association.event.Event;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.association.event.EventDetailsActivity;
import be.ugent.zeus.hydra.feed.HomeFeedAdapter;
import be.ugent.zeus.hydra.feed.SwipeDismissableViewHolder;
import be.ugent.zeus.hydra.feed.commands.DisableAssociationCommand;
import be.ugent.zeus.hydra.feed.cards.PriorityUtils;
import be.ugent.zeus.hydra.feed.cards.CardViewHolder;
import be.ugent.zeus.hydra.utils.DateUtils;

/**
 * View holder for cards containing events.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class EventCardViewHolder extends CardViewHolder implements SwipeDismissableViewHolder {

    private static final String TAG = "EventCardViewHolder";

    private final TextView start;
    private final TextView title;
    private final TextView association;
    private final ImageView imageView;

    private Event event;

    public EventCardViewHolder(View v, HomeFeedAdapter adapter) {
        super(v, adapter);
        title = v.findViewById(R.id.name);
        association = v.findViewById(R.id.association);
        start = v.findViewById(R.id.starttime);
        imageView = v.findViewById(R.id.imageView);
    }

    @Override
    public void populate(final Card card) {
        super.populate(card);
        event = card.<EventCard>checkCard(Card.Type.ACTIVITY).getEvent();

        title.setText(event.getTitle());
        association.setText(event.getLocation());
        start.setText(DateUtils.relativeDateTimeString(event.getStart(), itemView.getContext(), false));
        String description = itemView.getResources().getString(R.string.home_card_description);
        toolbar.setTitle(String.format(description, event.getAssociation().getInternalName()));

        PriorityUtils.loadThumbnail(itemView.getContext(), event.getAssociation().getImageLink(), imageView);

        itemView.setOnClickListener(v -> {
            if (itemView.getContext() instanceof Activity) {
                EventDetailsActivity.launchWithAnimation(((Activity) itemView.getContext()), imageView, "logo", event);
            } else {
                itemView.getContext().startActivity(EventDetailsActivity.start(itemView.getContext(), event));
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        // TODO: should this crash?
        if (event == null) {
            Log.e(TAG, "The event was null when menu was called. Ignoring.");
            return super.onMenuItemClick(item);
        }
        switch (item.getItemId()) {
            case R.id.menu_hide_association:
                adapter.getCompanion().executeCommand(new DisableAssociationCommand(event.getAssociation()));
                return true;
            default:
                return super.onMenuItemClick(item);
        }
    }
}