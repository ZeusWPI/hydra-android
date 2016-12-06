package be.ugent.zeus.hydra.homefeed.content.event;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.EventDetailActivity;
import be.ugent.zeus.hydra.homefeed.HomeFeedAdapter;
import be.ugent.zeus.hydra.homefeed.content.HideableViewHolder;
import be.ugent.zeus.hydra.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.models.association.Event;
import be.ugent.zeus.hydra.utils.DateUtils;
import com.squareup.picasso.Picasso;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Created by feliciaan on 06/04/16.
 */
public class EventCardViewHolder extends HideableViewHolder {

    private TextView start;
    private TextView title;
    private TextView association;
    private ImageView imageView;

    public EventCardViewHolder(View v, HomeFeedAdapter adapter) {
        super(v, adapter);
        title = $(v, R.id.name);
        association = $(v, R.id.association);
        start = $(v, R.id.starttime);
        imageView = $(v, R.id.imageView);
    }

    @Override
    public void populate(final HomeCard card) {

        final Event event = card.<EventCard>checkCard(HomeCard.CardType.ACTIVITY).getEvent();

        title.setText(event.getTitle());
        association.setText(event.getLocation());
        start.setText(DateUtils.relativeDateTimeString(event.getStart(), itemView.getContext(), false));
        String description = itemView.getResources().getString(R.string.home_card_description);
        toolbar.setTitle(String.format(description, event.getAssociation().getDisplayName()));

        Picasso.with(itemView.getContext()).load(event.getAssociation().getImageLink()).fit().centerInside().into(imageView);

        itemView.setOnClickListener(v -> EventDetailActivity.launchWithAnimation(((Activity) itemView.getContext()), imageView, "logo", event));

        debugPriority(card);
        toolbar.setMenu(R.menu.now_toolbar_association_event);
        toolbar.setOnClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_hide:
                    adapter.disableCardType(card.getCardType());
                    return true;
                case R.id.menu_hide_association:
                    adapter.disableAssociation(event.getAssociation());
                    return true;
                default:
                    return false;
            }
        });
    }
}