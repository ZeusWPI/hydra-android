package be.ugent.zeus.hydra.recyclerviewholder.home;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.CardModel;
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
    public void populate(CardModel card) {
        if (card.getCardType() != CardModel.CardType.SPECIAL_EVENT) {
            return; //TODO: give warning
        }

        final SpecialEvent event = (SpecialEvent) card;

        TextView titleView = $(view, R.id.title);
        titleView.setText(event.getName());

        TextView textView = $(view, R.id.text);
        textView.setText(event.getSimpleText());

        ImageView imageView = $(view, R.id.imageView);
        Picasso.with(view.getContext()).load(event.getImage()).into(imageView);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.getLink()));
                view.getContext().startActivity(browserIntent);
            }
        });
    }
}
