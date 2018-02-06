package be.ugent.zeus.hydra.ui.main.homefeed.content;

import android.annotation.SuppressLint;
import android.support.annotation.CallSuper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.common.ui.widgets.NowToolbar;
import be.ugent.zeus.hydra.feed.Card;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedAdapter;
import be.ugent.zeus.hydra.ui.main.homefeed.SwipeDismissableViewHolder;
import be.ugent.zeus.hydra.ui.main.homefeed.commands.DisableIndividualCard;
import be.ugent.zeus.hydra.ui.main.homefeed.commands.DisableTypeCommand;

/**
 * View holder for cards that are hideable, using the {@link NowToolbar}.
 *
 * By default, swiping the card away is enabled.
 *
 * @author Niko Strijbol
 */
public abstract class FeedViewHolder extends DataViewHolder<Card> implements SwipeDismissableViewHolder, PopupMenu.OnMenuItemClickListener {

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
            ((ViewGroup) itemView).addView(priority);
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

        toolbar.setOnMenuClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        // TODO: should this crash?
        if (card == null) {
            Log.e(TAG, "Card was null when menu was called. Ignoring.");
            return false;
        }
        HomeFeedAdapter.AdapterCompanion companion = adapter.getCompanion();
        switch (item.getItemId()) {
            case R.id.menu_hide_type:
                companion.executeCommand(new DisableTypeCommand(card.getCardType()));
                return true;
            case R.id.menu_hide_card:
                companion.executeCommand(new DisableIndividualCard(card));
                return true;
            default:
                return false;
        }
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