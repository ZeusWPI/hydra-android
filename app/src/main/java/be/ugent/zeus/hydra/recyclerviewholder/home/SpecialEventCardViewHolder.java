package be.ugent.zeus.hydra.recyclerviewholder.home;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.models.HomeCard;
import be.ugent.zeus.hydra.models.specialevent.SpecialEvent;

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
        if (card.getCardType() != HomeCardAdapter.HomeType.SPECIALEVENT) {
            return; //TODO: give warning
        }

        final SpecialEvent event = (SpecialEvent) card;

        TextView titleView = (TextView) view.findViewById(R.id.title);
        titleView.setText(event.getName());

        TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText(event.getSimpleText());

        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
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
