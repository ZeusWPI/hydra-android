package be.ugent.zeus.hydra.recyclerview.viewholder.minerva;

import android.content.Intent;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.minerva.AnnouncementActivity;
import be.ugent.zeus.hydra.models.minerva.Announcement;
import be.ugent.zeus.hydra.recyclerview.viewholder.AbstractViewHolder;
import be.ugent.zeus.hydra.utils.DateUtils;

import java.util.Locale;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 * @version 15/07/2016
 */
public class AnnouncementViewHolder extends AbstractViewHolder<Announcement> {

    private TextView title;
    private TextView subtitle;
    private View parent;

    public AnnouncementViewHolder(View itemView) {
        super(itemView);
        title = $(itemView, R.id.title);
        parent = $(itemView, R.id.parent_layout);
        subtitle = $(itemView, R.id.subtitle);
    }

    @Override
    public void populate(final Announcement data) {
        title.setText(data.getTitle());
        String infoText = String.format(new Locale("nl"), "%s door %s",
                DateUtils.relativeDateString(data.getDate(), itemView.getContext()),
                data.getLecturer());
        subtitle.setText(infoText);

        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(itemView.getContext(), AnnouncementActivity.class);
                intent.putExtra(AnnouncementActivity.PARCEL_NAME, (Parcelable) data);
                itemView.getContext().startActivity(intent);
            }
        });
    }
}
