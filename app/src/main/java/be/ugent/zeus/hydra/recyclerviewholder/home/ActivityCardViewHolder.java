package be.ugent.zeus.hydra.recyclerviewholder.home;

import android.content.Intent;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.EventDetailsActivity;
import be.ugent.zeus.hydra.models.CardModel;
import be.ugent.zeus.hydra.models.association.AssociationActivity;
import com.squareup.picasso.Picasso;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Created by feliciaan on 06/04/16.
 */
public class ActivityCardViewHolder extends AbstractViewHolder {
    private final View view;
    private TextView start;
    private TextView title;
    private TextView association;
    private ImageView imageView;

    public ActivityCardViewHolder(View v) {
        super(v);
        this.view = v;
        title = $(v, R.id.name);
        association = $(v, R.id.association);
        start = $(v, R.id.starttime);
        imageView = $(v, R.id.imageView);
    }

    @Override
    public void populate(CardModel card) {
        if (card.getCardType() != CardModel.CardType.ACTIVITY) {
            return; //TODO: do warnings or something
        }

        final AssociationActivity activity = (AssociationActivity) card;

        //TODO: make pretty

        title.setText(activity.title);
        association.setText(activity.association.display_name);
        start.setText(DateUtils.getRelativeDateTimeString(view.getContext(), activity.start.getTime(), DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0));

        Picasso.with(view.getContext()).load(activity.association.getImageLink()).into(imageView);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), EventDetailsActivity.class);
                intent.putExtra("associationActivity", (Parcelable) activity);
                view.getContext().startActivity(intent);
            }
        });

    }
}