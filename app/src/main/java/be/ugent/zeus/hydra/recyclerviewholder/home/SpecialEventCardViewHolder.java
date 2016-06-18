package be.ugent.zeus.hydra.recyclerviewholder.home;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.cards.SpecialEventCard;
import be.ugent.zeus.hydra.models.specialevent.SpecialEvent;
import com.squareup.picasso.Picasso;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Created by feliciaan on 06/04/16.
 */
public class SpecialEventCardViewHolder extends AbstractViewHolder {
    private View view;

    public SpecialEventCardViewHolder(View itemView) {
        super(itemView);
        this.view = itemView;
    }

    @Override
    public void populate(HomeCard card) {
        if (card.getCardType() != HomeCard.CardType.SPECIAL_EVENT) {
            return; //TODO: give warning
        }

        final SpecialEventCard eventCard = (SpecialEventCard) card;
        final SpecialEvent specialEvent = eventCard.getSpecialEvent();

        TextView titleView = $(view, R.id.title);
        titleView.setText(specialEvent.getName());

        TextView textView = $(view, R.id.text);
        textView.setText(specialEvent.getSimpleText());

        ImageView imageView = $(view, R.id.imageView);
        Picasso.with(view.getContext()).load(specialEvent.getImage()).into(imageView);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(specialEvent.getLink()));
                view.getContext().startActivity(browserIntent);
            }
        });
    }
}
