package be.ugent.zeus.hydra.recyclerview.viewholder.minerva;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.minerva.AnnouncementActivity;
import be.ugent.zeus.hydra.data.models.minerva.Announcement;
import be.ugent.zeus.hydra.recyclerview.viewholder.DataViewHolder;
import be.ugent.zeus.hydra.utils.DateUtils;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class AnnouncementViewHolder extends DataViewHolder<Announcement> {

    private TextView title;
    private TextView subtitle;

    public AnnouncementViewHolder(View itemView) {
        super(itemView);
        title = $(itemView, R.id.title);
        subtitle = $(itemView, R.id.subtitle);
    }

    @Override
    public void populate(final Announcement data) {
        title.setText(data.getTitle());
        String infoText = itemView.getContext().getString(R.string.agenda_subtitle,
                DateUtils.relativeDateTimeString(data.getDate(), itemView.getContext(), false),
                data.getLecturer());
        subtitle.setText(infoText);

        if(data.isRead()) {
            markAsRead();
        } else {
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.white));
        }

        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AnnouncementActivity.class);
            intent.putExtra(AnnouncementActivity.ARG_ANNOUNCEMENT, (Parcelable) data);
            markAsRead();
            v.getContext().startActivity(intent);
        });
    }

    private void markAsRead() {
        //Due to a bug on older android versions, we need to set the padding again.
        int top = itemView.getPaddingTop();
        int right = itemView.getPaddingRight();
        int bottom = itemView.getPaddingBottom();
        int left = itemView.getPaddingLeft();
        TypedValue outValue = new TypedValue();
        itemView.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        itemView.setBackgroundResource(outValue.resourceId);
        itemView.setPadding(left, top, right, bottom);
    }
}