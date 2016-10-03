package be.ugent.zeus.hydra.recyclerview.viewholder;

import android.content.Intent;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.ActivityDetailActivity;
import be.ugent.zeus.hydra.models.association.Activity;
import org.threeten.bp.format.DateTimeFormatter;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * View holder for an event in the event tab.
 *
 * @author Niko Strijbol
 */
public class ActivityViewHolder extends DataViewHolder<Activity> {

    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private TextView start;
    private TextView title;
    private TextView association;

    public ActivityViewHolder(View v) {
        super(v);
        title = $(v, R.id.name);
        association = $(v, R.id.association);
        start = $(v, R.id.starttime);
    }

    public void populate(final Activity activity) {
        title.setText(activity.getTitle());
        association.setText(activity.getAssociation().getDisplayName());
        start.setText(activity.getLocalStart().format(HOUR_FORMATTER));
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(itemView.getContext(), ActivityDetailActivity.class);
                intent.putExtra(ActivityDetailActivity.PARCEL_EVENT, (Parcelable) activity);
                itemView.getContext().startActivity(intent);
            }
        });
    }
}
