package be.ugent.zeus.hydra.ui.main.homefeed.content;

import android.annotation.SuppressLint;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;
import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.domain.models.feed.Card;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedAdapter;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.ui.common.widgets.NowToolbar;

/**
 * View holder for cards that are hideable, using the {@link NowToolbar}.
 *
 * @author Niko Strijbol
 */
public abstract class FeedViewHolder extends DataViewHolder<Card> {

    protected final HomeFeedAdapter adapter;
    protected final NowToolbar toolbar;

    private TextView priority;

    public FeedViewHolder(View itemView, HomeFeedAdapter adapter) {
        super(itemView);
        this.adapter = adapter;
        toolbar = itemView.findViewById(R.id.card_now_toolbar);

        if (BuildConfig.DEBUG && BuildConfig.DEBUG_HOME_STREAM_PRIORITY) {
            priority = new TextView(itemView.getContext());
            CardView cardView = (CardView) itemView;
            cardView.addView(priority);
        }
    }

    @Override
    public void populate(Card card) {
        toolbar.setOnClickListener(adapter.listener(card.getCardType()));
        debugPriority(card);
    }

    @SuppressLint("SetTextI18n")
    protected void debugPriority(Card card) {
        if(BuildConfig.DEBUG && BuildConfig.DEBUG_HOME_STREAM_PRIORITY) {
            priority.setText("Prioriteit: " + card.getPriority());
        }
    }
}