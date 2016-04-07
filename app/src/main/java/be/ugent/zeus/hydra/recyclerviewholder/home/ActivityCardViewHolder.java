package be.ugent.zeus.hydra.recyclerviewholder.home;

import android.content.Intent;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.Locale;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.AssociationActivityDetail;
import be.ugent.zeus.hydra.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.models.HomeCard;
import be.ugent.zeus.hydra.models.association.AssociationActivity;

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

        final AssociationActivity activity = (AssociationActivity) card;

        title.setText(activity.title);
        association.setText(activity.association.display_name);
        start.setText(DateUtils.getRelativeDateTimeString(view.getContext(), activity.start.getTime(), DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0));

        Picasso.with(view.getContext()).load("https://zeus.ugent.be/hydra/api/2.0/association/logo/" + activity.association.internal_name.toLowerCase() + ".png").into(imageView);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), AssociationActivityDetail.class);
                intent.putExtra("associationActivity", activity);
                view.getContext().startActivity(intent);
            }
        });

    }
}
