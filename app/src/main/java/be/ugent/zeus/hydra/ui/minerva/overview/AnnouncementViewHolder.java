package be.ugent.zeus.hydra.ui.minerva.overview;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.minerva.Announcement;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.ui.minerva.AnnouncementActivity;
import be.ugent.zeus.hydra.utils.DateUtils;

import static be.ugent.zeus.hydra.ui.common.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class AnnouncementViewHolder extends DataViewHolder<Announcement> {

    private TextView title;
    private TextView subtitle;
    private View clickingView;

    public AnnouncementViewHolder(View itemView) {
        super(itemView);
        title = $(itemView, R.id.title);
        subtitle = $(itemView, R.id.subtitle);
        clickingView = $(itemView, R.id.clickable_view);
    }

    @Override
    public void populate(final Announcement data) {
        title.setText(data.getTitle());
        String infoText = itemView.getContext().getString(R.string.agenda_subtitle,
                DateUtils.relativeDateTimeString(data.getDate(), itemView.getContext(), false),
                data.getLecturer());
        subtitle.setText(infoText);

        if (data.isRead()) {
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), android.R.color.transparent));
        } else {
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.white));
        }

        clickingView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AnnouncementActivity.class);
            intent.putExtra(AnnouncementActivity.ARG_ANNOUNCEMENT, (Parcelable) data);
            v.getContext().startActivity(intent);
        });
    }
}