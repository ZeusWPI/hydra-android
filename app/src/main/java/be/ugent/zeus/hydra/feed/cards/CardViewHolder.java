/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra.feed.cards;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.CallSuper;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.common.ui.widgets.NowToolbar;
import be.ugent.zeus.hydra.feed.HomeFeedAdapter;
import be.ugent.zeus.hydra.feed.SwipeDismissableViewHolder;
import be.ugent.zeus.hydra.feed.commands.DisableIndividualCard;
import be.ugent.zeus.hydra.feed.commands.DisableTypeCommand;

/**
 * View holder for cards that are hideable, using the {@link NowToolbar}.
 * <p>
 * By default, swiping the card away is enabled.
 *
 * @author Niko Strijbol
 */
public abstract class CardViewHolder extends DataViewHolder<Card> implements SwipeDismissableViewHolder, MenuHandler {

    private static final String TAG = "CardViewHolder";

    protected final HomeFeedAdapter adapter;
    protected final NowToolbar toolbar;

    private TextView priority;
    private Card card;

    public CardViewHolder(View itemView, HomeFeedAdapter adapter) {
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
    public void onCreateMenu(Menu menu) {
        // Do nothing by default.
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
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
            Log.w(TAG, "onSwiped: card is null somehow. Ignoring!");
        }
    }

    @Override
    public boolean isSwipeEnabled() {
        return card != null;
    }
}
