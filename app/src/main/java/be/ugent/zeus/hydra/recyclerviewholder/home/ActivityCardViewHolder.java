package be.ugent.zeus.hydra.recyclerviewholder.home;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.AssociationActivityDetail;
import be.ugent.zeus.hydra.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.models.cards.AssociationActivityCard;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.association.AssociationActivity;
import be.ugent.zeus.hydra.utils.DateUtils;

/**
 * Created by feliciaan on 06/04/16.
 */
public class ActivityCardViewHolder extends AbstractViewHolder {
    private TextView start;
    private TextView title;
    private TextView association;
    private ImageView imageView;

    public ActivityCardViewHolder(View v) {
        super(v);
        title = (TextView) v.findViewById(R.id.name);
        association = (TextView) v.findViewById(R.id.association);
        start = (TextView) v.findViewById(R.id.starttime);
        imageView = (ImageView) v.findViewById(R.id.imageView);
    }

    @Override
    public void populate(HomeCard card) {
        if (card.getCardType() != HomeCardAdapter.HomeType.ACTIVITY) {
            return; //TODO: do warnings or something
        }

        final AssociationActivityCard activityCard = (AssociationActivityCard) card;
        final AssociationActivity activity = activityCard.getAssociationActivity();

        //TODO: make pretty
        title.setText(activity.title);
        association.setText(activity.association.display_name);
        start.setText(DateUtils.relativeDateString(activity.getStartDate(), itemView.getContext()));

        Picasso.with(itemView.getContext()).load(activity.association.getImageLink()).into(imageView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(itemView.getContext(), AssociationActivityDetail.class);
                intent.putExtra("associationActivity", activity);
                itemView.getContext().startActivity(intent);
            }
        });

    }
}
