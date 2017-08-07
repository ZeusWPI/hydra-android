package be.ugent.zeus.hydra.ui.main.homefeed.content.event;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.EventDetailActivity;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedAdapter;
import be.ugent.zeus.hydra.ui.main.homefeed.commands.DisableAssociationCommand;
import be.ugent.zeus.hydra.ui.main.homefeed.commands.DisableTypeCommand;
import be.ugent.zeus.hydra.ui.main.homefeed.content.FeedUtils;
import be.ugent.zeus.hydra.ui.main.homefeed.content.FeedViewHolder;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.data.models.association.Event;
import be.ugent.zeus.hydra.utils.DateUtils;

/**
 * View holder for cards containing events.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class EventCardViewHolder extends FeedViewHolder {

    private final TextView start;
    private final TextView title;
    private final TextView association;
    private final ImageView imageView;

    public EventCardViewHolder(View v, HomeFeedAdapter adapter) {
        super(v, adapter);
        title = v.findViewById(R.id.name);
        association = v.findViewById(R.id.association);
        start = v.findViewById(R.id.starttime);
        imageView = v.findViewById(R.id.imageView);
    }

    @Override
    public void populate(final HomeCard card) {

        final Event event = card.<EventCard>checkCard(HomeCard.CardType.ACTIVITY).getEvent();

        title.setText(event.getTitle());
        association.setText(event.getLocation());
        start.setText(DateUtils.relativeDateTimeString(event.getStart(), itemView.getContext(), false));
        String description = itemView.getResources().getString(R.string.home_card_description);
        toolbar.setTitle(String.format(description, event.getAssociation().getInternalName()));

        FeedUtils.loadThumbnail(itemView.getContext(), event.getAssociation().getImageLink(), imageView);

        itemView.setOnClickListener(v -> EventDetailActivity.launchWithAnimation(((Activity) itemView.getContext()), imageView, "logo", event));

        debugPriority(card);
        toolbar.setMenu(R.menu.now_toolbar_association_event);
        toolbar.setOnClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_hide:
                    adapter.getCompanion().executeCommand(new DisableTypeCommand(card.getCardType()));
                    return true;
                case R.id.menu_hide_association:
                    adapter.getCompanion().executeCommand(new DisableAssociationCommand(event.getAssociation()));
                    return true;
                default:
                    return false;
            }
        });
    }
}