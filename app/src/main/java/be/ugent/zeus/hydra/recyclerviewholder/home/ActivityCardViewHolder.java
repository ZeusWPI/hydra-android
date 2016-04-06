package be.ugent.zeus.hydra.recyclerviewholder.home;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

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
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public ActivityCardViewHolder(View v) {
        super(v);
        this.view = v;
        title = (TextView) v.findViewById(R.id.name);
        association = (TextView) v.findViewById(R.id.association);
        start = (TextView) v.findViewById(R.id.starttime);
    }

    @Override
    public void populate(HomeCard card) {
        if (card.getCardType() != HomeCardAdapter.HomeType.ACTIVITY) {
            return; //TODO: do warnings or something
        }

        final AssociationActivity activity = (AssociationActivity) card;

        title.setText(activity.title);
        association.setText(activity.association.display_name);
        start.setText(dateFormatter.format(activity.start));
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
