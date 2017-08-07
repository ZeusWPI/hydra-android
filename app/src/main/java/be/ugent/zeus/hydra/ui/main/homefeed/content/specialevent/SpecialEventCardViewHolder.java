package be.ugent.zeus.hydra.ui.main.homefeed.content.specialevent;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.specialevent.SpecialEvent;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedAdapter;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedFragment;
import be.ugent.zeus.hydra.ui.main.homefeed.SwipeDismissableViewHolder;
import be.ugent.zeus.hydra.ui.main.homefeed.commands.DisableIndividualCard;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

/**
 * Home feed view holder for special events.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class SpecialEventCardViewHolder extends DataViewHolder<HomeCard> implements SwipeDismissableViewHolder {

    private final TextView title;
    private final TextView text;
    private final ImageView image;
    private final HomeFeedAdapter.AdapterCompanion companion;

    private SpecialEvent event;

    public SpecialEventCardViewHolder(View itemView, HomeFeedAdapter.AdapterCompanion companion) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        text = itemView.findViewById(R.id.text);
        image = itemView.findViewById(R.id.image);
        this.companion = companion;
    }

    @Override
    public void populate(HomeCard card) {

        SpecialEventCard eventCard = card.checkCard(HomeCard.CardType.SPECIAL_EVENT);
        event = eventCard.getSpecialEvent();

        title.setText(event.getName());
        text.setText(event.getSimpleText());
        Picasso.with(itemView.getContext()).load(event.getImage()).into(image);

        itemView.setOnClickListener(v -> NetworkUtils.maybeLaunchIntent(v.getContext(), event.getViewIntent()));
    }

    @Override
    public void onSwiped() {
        // Do nothing for now!
        if (event != null) {
            companion.executeCommand(
                    new DisableIndividualCard(
                            HomeFeedFragment.PREF_DISABLED_SPECIALS,
                            String.valueOf(event.getId()),
                            HomeCard.CardType.SPECIAL_EVENT
                    )
            );
        }

    }

    @Override
    public boolean isSwipeEnabled() {
        return true;
    }
}