package be.ugent.zeus.hydra.homefeed.content;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.homefeed.HomeFeedAdapter;
import be.ugent.zeus.hydra.recyclerview.viewholder.DataViewHolder;
import be.ugent.zeus.hydra.views.NowToolbar;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * View holder for cards that are hideable, using the {@link NowToolbar}.
 *
 * @author Niko Strijbol
 */
public abstract class HideableViewHolder extends DataViewHolder<HomeCard> {

    protected final HomeFeedAdapter adapter;
    protected final NowToolbar toolbar;

    protected final TextView priority;

    public HideableViewHolder(View itemView, HomeFeedAdapter adapter) {
        super(itemView);
        this.adapter = adapter;
        toolbar = $(itemView, R.id.card_now_toolbar);
        priority = new TextView(itemView.getContext());
        CardView cardView = (CardView) itemView;
        cardView.addView(priority);
    }

    @Override
    public void populate(HomeCard card) {
        toolbar.setOnClickListener(adapter.listener(card.getCardType()));
        priority.setText("Priority: " + card.getPriority());
    }
}