package be.ugent.zeus.hydra.ui.main.homefeed.content.specialevent;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.data.models.specialevent.SpecialEvent;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

/**
 * Home feed view holder for special events.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class SpecialEventCardViewHolder extends DataViewHolder<HomeCard> {

    private final TextView title;
    private final TextView text;
    private final ImageView image;

    public SpecialEventCardViewHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        text = itemView.findViewById(R.id.text);
        image = itemView.findViewById(R.id.image);
    }

    @Override
    public void populate(HomeCard card) {

        SpecialEventCard eventCard = card.checkCard(HomeCard.CardType.SPECIAL_EVENT);
        SpecialEvent specialEvent = eventCard.getSpecialEvent();

        title.setText(specialEvent.getName());
        text.setText(specialEvent.getSimpleText());
        Picasso.with(itemView.getContext()).load(specialEvent.getImage()).into(image);

        itemView.setOnClickListener(v -> NetworkUtils.maybeLaunchIntent(v.getContext(), specialEvent.getViewIntent()));
    }
}