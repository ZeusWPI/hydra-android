package be.ugent.zeus.hydra.recyclerview.viewholder.minerva;

import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.minerva.AnnouncementActivity;
import be.ugent.zeus.hydra.models.minerva.Announcement;
import be.ugent.zeus.hydra.recyclerview.viewholder.DataViewHolder;
import be.ugent.zeus.hydra.utils.DateUtils;

import java.util.Locale;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class AnnouncementViewHolder extends DataViewHolder<Announcement> {

    private TextView title;
    private TextView subtitle;
    private Fragment fragment;

    public AnnouncementViewHolder(View itemView) {
        this(itemView, null);
    }

    public AnnouncementViewHolder(View itemView, @Nullable Fragment fragment) {
        super(itemView);
        title = $(itemView, R.id.title);
        subtitle = $(itemView, R.id.subtitle);
        this.fragment = fragment;
    }

    @Override
    public void populate(final Announcement data) {
        title.setText(data.getTitle());
        String infoText = String.format(new Locale("nl"), "%s door %s",
                DateUtils.relativeDateTimeString(data.getDate(), itemView.getContext(), false),
                data.getLecturer());
        subtitle.setText(infoText);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(itemView.getContext(), AnnouncementActivity.class);
                intent.putExtra(AnnouncementActivity.ARG_ANNOUNCEMENT, (Parcelable) data);
                if(fragment == null) {
                    itemView.getContext().startActivity(intent);
                } else {
                    fragment.startActivityForResult(intent, AnnouncementActivity.RESULT_ANNOUNCEMENT);
                }
            }
        });
    }
}
