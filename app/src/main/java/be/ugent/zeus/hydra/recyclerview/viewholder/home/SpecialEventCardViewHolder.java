package be.ugent.zeus.hydra.recyclerview.viewholder.home;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.sko.OverviewActivity;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.cards.SpecialEventCard;
import be.ugent.zeus.hydra.models.specialevent.SpecialEvent;
import be.ugent.zeus.hydra.recyclerview.viewholder.DataViewHolder;
import com.squareup.picasso.Picasso;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Created by feliciaan on 06/04/16.
 */
public class SpecialEventCardViewHolder extends DataViewHolder<HomeCard> {

    public SpecialEventCardViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void populate(HomeCard card) {
        if (card.getCardType() != HomeCard.CardType.SPECIAL_EVENT) {
            return; //TODO: give warning
        }

        final SpecialEventCard eventCard = (SpecialEventCard) card;
        final SpecialEvent specialEvent = eventCard.getSpecialEvent();

        TextView titleView = $(itemView, R.id.title);
        titleView.setText(specialEvent.getName());

        TextView textView = $(itemView, R.id.text);
        textView.setText(specialEvent.getSimpleText());

        ImageView imageView = $(itemView, R.id.imageView);
        Picasso.with(itemView.getContext()).load(specialEvent.getImage()).into(imageView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if(specialEvent.isSko()) {
                    intent = new Intent(itemView.getContext(), OverviewActivity.class);
                } else {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(specialEvent.getLink()));
                }
                itemView.getContext().startActivity(intent);
            }
        });
    }
}
