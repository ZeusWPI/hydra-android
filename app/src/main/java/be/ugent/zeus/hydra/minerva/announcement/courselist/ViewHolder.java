package be.ugent.zeus.hydra.minerva.announcement.courselist;

import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.ResultStarter;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.minerva.announcement.Announcement;
import be.ugent.zeus.hydra.minerva.announcement.SingleAnnouncementActivity;
import be.ugent.zeus.hydra.utils.DateUtils;

/**
 * @author Niko Strijbol
 */
class ViewHolder extends DataViewHolder<Announcement> {

    private final TextView title;
    private final TextView subtitle;
    private final View clickingView;
    private final ResultStarter resultStarter;

    ViewHolder(View itemView, ResultStarter starter) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        subtitle = itemView.findViewById(R.id.subtitle);
        clickingView = itemView.findViewById(R.id.clickable_view);
        resultStarter = starter;
    }

    @Override
    public void populate(final Announcement data) {
        title.setText(data.getTitle());
        String infoText = itemView.getContext().getString(R.string.deprecated_dot_seperated,
                DateUtils.relativeDateTimeString(data.getDate(), itemView.getContext(), false),
                data.getLecturer());
        subtitle.setText(infoText);

        if (data.isRead()) {
            itemView.setBackgroundColor(Color.TRANSPARENT);
        } else {
            itemView.setBackgroundResource(R.color.hydra_item_selected_background);
        }

        clickingView.setOnClickListener(v -> {
            Intent intent = new Intent(resultStarter.getContext(), SingleAnnouncementActivity.class);
            intent.putExtra(SingleAnnouncementActivity.ARG_ANNOUNCEMENT, (Parcelable) data);
            resultStarter.startActivityForResult(intent, resultStarter.getRequestCode());
        });
    }
}