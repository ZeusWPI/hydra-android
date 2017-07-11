package be.ugent.zeus.hydra.ui.main.minerva;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.minerva.Announcement;
import be.ugent.zeus.hydra.ui.common.recyclerview.ResultStarter;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.ui.minerva.AnnouncementActivity;
import be.ugent.zeus.hydra.utils.DateUtils;

import static be.ugent.zeus.hydra.ui.common.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class AnnouncementsViewHolder extends DataViewHolder<Announcement> {

    private final TextView title;
    private final TextView subtitle;
    private final ResultStarter resultStarter;

    public AnnouncementsViewHolder(View itemView, ResultStarter starter) {
        super(itemView);
        title = $(itemView, R.id.title);
        subtitle = $(itemView, R.id.subtitle);
        resultStarter = starter;
    }

    @Override
    public void populate(final Announcement data) {
        title.setText(data.getTitle());
        String infoText = itemView.getContext().getString(R.string.agenda_subtitle,
                data.getCourse().getTitle(),
                DateUtils.relativeDateTimeString(data.getDate(), itemView.getContext(), false));
        subtitle.setText(infoText);

        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(resultStarter.getContext(), AnnouncementActivity.class);
            intent.putExtra(AnnouncementActivity.ARG_ANNOUNCEMENT, (Parcelable) data);
            resultStarter.startActivityForResult(intent, resultStarter.getRequestCode());
        });
    }
}