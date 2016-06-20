package be.ugent.zeus.hydra.recyclerviewholder.home;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.cards.SpecialEventCard;
import be.ugent.zeus.hydra.models.specialevent.SpecialEvent;

/**
 * Created by feliciaan on 06/04/16.
 */
public class SpecialEventCardViewHolder extends AbstractViewHolder {
    private TextView titleView;
    private TextView textView;
    private ImageView imageView;

    public SpecialEventCardViewHolder(View itemView) {
        super(itemView);

        titleView = (TextView) itemView.findViewById(R.id.title);
        textView = (TextView) itemView.findViewById(R.id.text);
        imageView = (ImageView) itemView.findViewById(R.id.imageView);
    }

    @Override
    public void populate(HomeCard card) {
        if (card.getCardType() != HomeCardAdapter.HomeType.SPECIALEVENT) {
            return; //TODO: give warning
        }

        final SpecialEventCard eventCard = (SpecialEventCard) card;
        final SpecialEvent specialEvent = eventCard.getSpecialEvent();

        titleView.setText(specialEvent.getName());

        textView.setText(specialEvent.getSimpleText());

        Picasso.with(itemView.getContext()).load(specialEvent.getImage()).into(imageView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(specialEvent.getLink()));
                itemView.getContext().startActivity(browserIntent);
            }
        });
    }
}
