package be.ugent.zeus.hydra.ui.main.homefeed.content;

import android.annotation.SuppressLint;
import android.support.annotation.CallSuper;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.domain.models.feed.Card;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.ui.common.widgets.NowToolbar;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedAdapter;
import be.ugent.zeus.hydra.ui.main.homefeed.SwipeDismissableViewHolder;
import be.ugent.zeus.hydra.ui.main.homefeed.commands.DisableIndividualCard;

/**
 * View holder for cards that are hideable, using the {@link NowToolbar}.
 *
 * By default, swiping the card away is enabled.
 *
 * @author Niko Strijbol
 */
public abstract class FeedViewHolder extends DataViewHolder<Card> implements SwipeDismissableViewHolder {

    private static final String TAG = "FeedViewHolder";

    protected final HomeFeedAdapter adapter;
    protected final NowToolbar toolbar;

    private TextView priority;
    private Card card;

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
    @CallSuper
    @SuppressLint("SetTextI18n")
    public void populate(Card card) {
        this.card = card;

        if (BuildConfig.DEBUG && BuildConfig.DEBUG_HOME_STREAM_PRIORITY) {
            priority.setText("Prioriteit: " + card.getPriority());
        }

        if (enableDefaultClickListener()) {
            toolbar.setOnClickListener(adapter.listener(card.getCardType()));
        }
    }

    protected boolean enableDefaultClickListener() {
        return true;
    }

    @Override
    public void onSwiped() {
        if (card != null) {
            adapter.getCompanion().executeCommand(new DisableIndividualCard(card));
        } else {
            // TODO: maybe this should crash?
            Log.w(TAG, "onSwiped: card is null somehow. Ignoring!");
        }
    }

    @Override
    public boolean isSwipeEnabled() {
        return card != null;
    }
}