package be.ugent.zeus.hydra.recyclerview.viewholder.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.cards.MinervaAnnouncementsCard;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.recyclerview.viewholder.minerva.AnnouncementViewHolder;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;
import static be.ugent.zeus.hydra.utils.ViewUtils.convertDpToPixelInt;

/**
 * Minerva home view holder
 *
 * We limit it to 5 announcements.
 */
public class MinervaAnnouncementViewHolder extends HideableViewHolder {

    private LinearLayout layout;

    public MinervaAnnouncementViewHolder(View v, HomeCardAdapter adapter) {
        super(v, adapter);

        layout = $(v, R.id.linear_layout);
    }

    @Override
    public void populate(HomeCard card) {

        MinervaAnnouncementsCard mCard = card.checkCard(HomeCard.CardType.MINERVA_ANNOUNCEMENT);

        toolbar.setTitle("Aankondigingen " + mCard.getCourse().getTitle());

        layout.removeAllViewsInLayout();

        for (int i = 0; i < 5 && i < mCard.getAnnouncements().size(); i++) {
            View view = LayoutInflater.from(itemView.getContext()).inflate(R.layout.item_minerva_home_announcement, null);

            AnnouncementViewHolder viewHolder = new AnnouncementViewHolder(view);
            viewHolder.populate(mCard.getAnnouncements().get(i));

            layout.addView(view);
        }

        if(mCard.getAnnouncements().size() >= 5) {
            TextView textView = new TextView(itemView.getContext());
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setText("Nog " + (mCard.getAnnouncements().size() - 5) + " verborgen aankondigingen.");
            textView.setPadding(0, convertDpToPixelInt(16, itemView.getContext()), 0, 0);
            layout.addView(textView);
        }

        super.populate(card);
    }
}