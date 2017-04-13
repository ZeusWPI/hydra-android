package be.ugent.zeus.hydra.ui.main.homefeed.content.minerva.announcement;

import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.minerva.overview.CourseActivity;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedAdapter;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HideableViewHolder;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.ui.minerva.overview.AnnouncementViewHolder;

import static be.ugent.zeus.hydra.ui.common.ViewUtils.$;
import static be.ugent.zeus.hydra.ui.common.ViewUtils.convertDpToPixelInt;

/**
 * Minerva home view holder. We limit it to 5 announcements.
 *
 * @author Niko Strijbol
 */
public class MinervaAnnouncementViewHolder extends HideableViewHolder {

    private final LinearLayout layout;
    private final CardView cardView;

    public MinervaAnnouncementViewHolder(View v, HomeFeedAdapter adapter) {
        super(v, adapter);
        layout = $(v, R.id.linear_layout);
        cardView = $(v, R.id.card_view);
    }

    @Override
    public void populate(HomeCard card) {
        super.populate(card);

        final MinervaAnnouncementsCard mCard = card.checkCard(HomeCard.CardType.MINERVA_ANNOUNCEMENT);

        String titleS = itemView.getContext().getString(R.string.home_feed_card_announcement_title, mCard.getCourse().getTitle());
        toolbar.setTitle(titleS);

        layout.removeAllViewsInLayout();

        for (int i = 0; i < 5 && i < mCard.getAnnouncements().size(); i++) {
            View view = LayoutInflater.from(layout.getContext()).inflate(R.layout.item_minerva_home_announcement, layout, false);

            AnnouncementViewHolder viewHolder = new AnnouncementViewHolder(view);
            viewHolder.populate(mCard.getAnnouncements().get(i));

            layout.addView(view);
        }

        if (mCard.getAnnouncements().size() >= 5) {
            TextView textView = new TextView(itemView.getContext());
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            textView.setText(itemView.getContext().getString(R.string.home_feed_card_announcement_more, mCard.getAnnouncements().size() - 5));
            textView.setPadding(0, convertDpToPixelInt(16, itemView.getContext()), 0, 0);
            layout.addView(textView);
        }

        cardView.setOnClickListener(v -> CourseActivity.start(v.getContext(), mCard.getCourse()));
    }
}