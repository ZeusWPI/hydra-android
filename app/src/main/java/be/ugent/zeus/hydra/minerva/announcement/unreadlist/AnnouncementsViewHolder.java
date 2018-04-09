package be.ugent.zeus.hydra.minerva.announcement.unreadlist;

import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.ResultStarter;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.MultiSelectAdapter;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.minerva.announcement.Announcement;
import be.ugent.zeus.hydra.minerva.announcement.SingleAnnouncementActivity;
import be.ugent.zeus.hydra.utils.DateUtils;

/**
 * @author Niko Strijbol
 */
class AnnouncementsViewHolder extends DataViewHolder<Announcement> {

    private final TextView title;
    private final TextView subtitle;
    private final View backgroundHolder;
    private final ResultStarter resultStarter;
    private final MultiSelectAdapter<Announcement> adapter;

    AnnouncementsViewHolder(View itemView, ResultStarter starter, MultiSelectAdapter<Announcement> adapter) {
        super(itemView);
        this.resultStarter = starter;
        this.adapter = adapter;
        title = itemView.findViewById(R.id.title);
        subtitle = itemView.findViewById(R.id.subtitle);
        backgroundHolder = itemView.findViewById(R.id.background_container);
    }

    @Override
    public void populate(final Announcement announcement) {

        toggleBackground();

        title.setText(announcement.getTitle());
        String infoText = itemView.getContext().getString(R.string.agenda_subtitle,
                announcement.getCourse().getTitle(),
                DateUtils.relativeDateTimeString(announcement.getDate(), itemView.getContext(), false));
        subtitle.setText(infoText);

        itemView.setOnClickListener(v -> {
            // When we are in select mode, we just toggle the items. Otherwise we open them.
            if (adapter.hasSelected()) {
                toggleSelected();
            } else {
                Intent intent = new Intent(resultStarter.getContext(), SingleAnnouncementActivity.class);
                intent.putExtra(SingleAnnouncementActivity.ARG_ANNOUNCEMENT, (Parcelable) announcement);
                resultStarter.startActivityForResult(intent, resultStarter.getRequestCode());
            }
        });

        itemView.setOnLongClickListener(v -> {
            toggleSelected();
            return true;
        });
    }

    private void toggleSelected() {
        adapter.setChecked(getAdapterPosition());
        toggleBackground();
    }

    private void toggleBackground() {
        if (adapter.isChecked(getAdapterPosition())) {
            backgroundHolder.setBackgroundColor(Color.WHITE);
        } else {
            backgroundHolder.setBackgroundColor(Color.TRANSPARENT);
        }
    }
}