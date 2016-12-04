package be.ugent.zeus.hydra.homefeed.content.minerva.announcement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.minerva.CourseActivity;
import be.ugent.zeus.hydra.homefeed.HomeFeedAdapter;
import be.ugent.zeus.hydra.homefeed.content.HideableViewHolder;
import be.ugent.zeus.hydra.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.recyclerview.viewholder.minerva.AnnouncementViewHolder;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;
import static be.ugent.zeus.hydra.utils.ViewUtils.convertDpToPixelInt;

/**
 * Minerva home view holder. We limit it to 5 announcements.
 *
 * @author Niko Strijbol
 */
public class MinervaAnnouncementViewHolder extends HideableViewHolder {

    private final LinearLayout layout;

    public MinervaAnnouncementViewHolder(View v, HomeFeedAdapter adapter) {
        super(v, adapter);
        layout = $(v, R.id.linear_layout);
    }

    @Override
    public void populate(HomeCard card) {
        super.populate(card);

        final MinervaAnnouncementsCard mCard = card.checkCard(HomeCard.CardType.MINERVA_ANNOUNCEMENT);

        toolbar.setTitle("Aankondigingen " + mCard.getCourse().getTitle());

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
            textView.setText("Bekijk nog " + (mCard.getAnnouncements().size() - 5) + " aankondigingen...");
            textView.setPadding(0, convertDpToPixelInt(16, itemView.getContext()), 0, 0);
            layout.addView(textView);
        }

        layout.setOnClickListener(v -> CourseActivity.start(v.getContext(), mCard.getCourse()));
    }
}