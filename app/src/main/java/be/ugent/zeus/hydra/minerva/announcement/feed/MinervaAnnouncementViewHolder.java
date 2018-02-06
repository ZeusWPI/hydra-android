package be.ugent.zeus.hydra.minerva.announcement.feed;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.ResultStarter;
import be.ugent.zeus.hydra.feed.Card;
import be.ugent.zeus.hydra.minerva.announcement.Announcement;
import be.ugent.zeus.hydra.minerva.announcement.SingleAnnouncementActivity;
import be.ugent.zeus.hydra.minerva.course.singlecourse.CourseActivity;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedAdapter;
import be.ugent.zeus.hydra.ui.main.homefeed.content.FeedViewHolder;
import be.ugent.zeus.hydra.utils.DateUtils;

import static be.ugent.zeus.hydra.common.ui.ViewUtils.convertDpToPixelInt;

/**
 * Minerva home view holder. We limit it to 5 announcements.
 *
 * @author Niko Strijbol
 */
public class MinervaAnnouncementViewHolder extends FeedViewHolder {

    private final static int MAX_DISPLAYED = 5;
    private final LinearLayout layout;
    private final CardView cardView;

    public MinervaAnnouncementViewHolder(View v, HomeFeedAdapter adapter) {
        super(v, adapter);
        layout = v.findViewById(R.id.linear_layout);
        cardView = v.findViewById(R.id.card_view);
    }

    @Override
    public void populate(Card card) {
        super.populate(card);

        final MinervaAnnouncementsCard mCard = card.checkCard(Card.Type.MINERVA_ANNOUNCEMENT);
        final Context context = itemView.getContext();

        toolbar.setTitle(context.getString(R.string.home_feed_card_announcement_title, mCard.getCourse().getTitle()));

        layout.removeAllViewsInLayout();

        ResultStarter starter = adapter.getCompanion();
        LayoutInflater inflater = LayoutInflater.from(layout.getContext());

        for (int i = 0; i < MAX_DISPLAYED && i < mCard.getAnnouncements().size(); i++) {
            View view = inflater.inflate(R.layout.item_minerva_home_announcement, layout, false);
            TextView title = view.findViewById(R.id.title);
            TextView subtitle = view.findViewById(R.id.subtitle);
            Announcement announcement = mCard.getAnnouncements().get(i);

            title.setText(announcement.getTitle());
            String infoText = itemView.getContext().getString(R.string.agenda_subtitle,
                    DateUtils.relativeDateTimeString(announcement.getDate(), itemView.getContext(), false),
                    announcement.getLecturer());
            subtitle.setText(infoText);

            view.setOnClickListener(v -> {
                Intent intent = new Intent(starter.getContext(), SingleAnnouncementActivity.class);
                intent.putExtra(SingleAnnouncementActivity.ARG_ANNOUNCEMENT, (Parcelable) announcement);
                starter.startActivityForResult(intent, starter.getRequestCode());
            });
            layout.addView(view);
        }

        if (mCard.getAnnouncements().size() > MAX_DISPLAYED) {
            TextView textView = new TextView(context);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            int remainingAnnouncements = mCard.getAnnouncements().size() - MAX_DISPLAYED;
            textView.setText(itemView.getResources().getQuantityString(R.plurals.home_feed_card_announcement_more, remainingAnnouncements, remainingAnnouncements));
            textView.setPadding(0, convertDpToPixelInt(16, context), 0, 0);
            layout.addView(textView);
        }

        cardView.setOnClickListener(v -> CourseActivity.startForResult(adapter.getCompanion(), mCard.getCourse(), CourseActivity.Tab.ANNOUNCEMENTS));
    }
}