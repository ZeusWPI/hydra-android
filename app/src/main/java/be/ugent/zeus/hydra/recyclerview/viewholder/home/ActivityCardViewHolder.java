package be.ugent.zeus.hydra.recyclerview.viewholder.home;

import android.content.Intent;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.ActivityDetailActivity;
import be.ugent.zeus.hydra.models.association.Activity;
import be.ugent.zeus.hydra.models.cards.AssociationActivityCard;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.utils.DateUtils;
import com.squareup.picasso.Picasso;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Created by feliciaan on 06/04/16.
 */
public class ActivityCardViewHolder extends HideableViewHolder {

    private TextView start;
    private TextView title;
    private TextView association;
    private ImageView imageView;

    public ActivityCardViewHolder(View v, HomeCardAdapter adapter) {
        super(v, adapter);
        title = $(v, R.id.name);
        association = $(v, R.id.association);
        start = $(v, R.id.starttime);
        imageView = $(v, R.id.imageView);
    }

    @Override
    public void populate(HomeCard card) {

        final Activity activity = card.<AssociationActivityCard>checkCard(HomeCard.CardType.ACTIVITY).getActivity();

        title.setText(activity.getTitle());
        association.setText(activity.getLocation());
        start.setText(DateUtils.relativeDateString(activity.getStartDate(), itemView.getContext()));
        String description = itemView.getResources().getString(R.string.home_card_description);
        toolbar.setTitle(String.format(description, activity.getAssociation().getDisplayName()));

        Picasso.with(itemView.getContext()).load(activity.getAssociation().getImageLink()).into(imageView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(itemView.getContext(), ActivityDetailActivity.class);
                intent.putExtra("associationActivity", (Parcelable) activity);
                itemView.getContext().startActivity(intent);
            }
        });

        super.populate(card);
    }
}