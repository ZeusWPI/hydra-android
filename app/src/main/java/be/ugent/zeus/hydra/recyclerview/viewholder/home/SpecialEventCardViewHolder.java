package be.ugent.zeus.hydra.recyclerview.viewholder.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.cards.SpecialEventCard;
import be.ugent.zeus.hydra.models.specialevent.SpecialEvent;
import be.ugent.zeus.hydra.recyclerview.viewholder.DataViewHolder;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 * @author feliciaan
 */
public class SpecialEventCardViewHolder extends DataViewHolder<HomeCard> {

    private TextView title;
    private TextView text;
    private ImageView image;
    private TextView position;

    public SpecialEventCardViewHolder(View itemView) {
        super(itemView);
        title = $(itemView, R.id.title);
        text = $(itemView, R.id.text);
        image = $(itemView, R.id.image);
        position = $(itemView, R.id.position);
    }

    @Override
    public void populate(HomeCard card) {
        final SpecialEventCard eventCard = card.checkCard(HomeCard.CardType.SPECIAL_EVENT);
        final SpecialEvent specialEvent = eventCard.getSpecialEvent();

        title.setText(specialEvent.getName());
        text.setText(specialEvent.getSimpleText());
        Picasso.with(itemView.getContext()).load(specialEvent.getImage()).into(image);

        position.setText(String.valueOf(card.getPriority()));

        itemView.setOnClickListener(v -> NetworkUtils.maybeLaunchIntent(v.getContext(), specialEvent.getViewIntent()));
    }
}