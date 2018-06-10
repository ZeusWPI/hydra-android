package be.ugent.zeus.hydra.feed.cards.implementations.minerva.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.minerva.calendar.itemdetails.AgendaActivity;
import be.ugent.zeus.hydra.feed.HomeFeedAdapter;
import be.ugent.zeus.hydra.feed.cards.CardViewHolder;
import be.ugent.zeus.hydra.minerva.calendar.AgendaItem;
import be.ugent.zeus.hydra.utils.DateUtils;

/**
 * Minerva agenda item.
 *
 * @author Niko Strijbol
 */
public class MinervaAgendaViewHolder extends CardViewHolder {

    private final LinearLayout layout;

    public MinervaAgendaViewHolder(View v, HomeFeedAdapter adapter) {
        super(v, adapter);
        layout = v.findViewById(R.id.linear_layout);
    }

    @Override
    public void populate(Card card) {
        super.populate(card);

        final MinervaAgendaCard mCard = card.checkCard(Card.Type.MINERVA_AGENDA);
        Context c = itemView.getContext();
        String titleS = c.getString(R.string.home_feed_agenda_title, DateUtils.getFriendlyDate(c, mCard.getDate()));
        toolbar.setTitle(titleS);

        layout.removeAllViewsInLayout();
        LayoutInflater inflater = LayoutInflater.from(layout.getContext());

        for (final AgendaItem item : mCard.getAgendaItems()) {

            View view = inflater.inflate(R.layout.item_minerva_home_announcement, layout, false);
            TextView title = view.findViewById(R.id.title);
            TextView subtitle = view.findViewById(R.id.subtitle);

            title.setText(item.getTitle());
            subtitle.setText(DateUtils.relativeTimeSpan(view.getContext(), item.getStartDate(), item.getEndDate()));

            layout.addView(view);

            view.setOnClickListener(v -> AgendaActivity.start(v.getContext(), item));
        }
    }
}