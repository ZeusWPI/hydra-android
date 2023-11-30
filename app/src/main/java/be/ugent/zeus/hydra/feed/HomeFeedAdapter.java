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

package be.ugent.zeus.hydra.feed;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.common.ui.recyclerview.ResultStarter;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.DiffAdapter;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.debug.DebugCardViewHolder;
import be.ugent.zeus.hydra.feed.cards.event.EventCardViewHolder;
import be.ugent.zeus.hydra.feed.cards.library.LibraryViewHolder;
import be.ugent.zeus.hydra.feed.cards.news.NewsItemViewHolder;
import be.ugent.zeus.hydra.feed.cards.resto.RestoCardViewHolder;
import be.ugent.zeus.hydra.feed.cards.schamper.SchamperViewHolder;
import be.ugent.zeus.hydra.feed.cards.specialevent.SpecialEventCardViewHolder;
import be.ugent.zeus.hydra.feed.cards.urgent.UrgentViewHolder;
import be.ugent.zeus.hydra.feed.commands.FeedCommand;

import static be.ugent.zeus.hydra.feed.cards.Card.Type.*;

/**
 * Adapter for {@link HomeFeedFragment}.
 *
 * @author feliciaan
 * @author Niko Strijbol
 */
public class HomeFeedAdapter extends DiffAdapter<Card, DataViewHolder<Card>> {

    private final AdapterCompanion companion;

    HomeFeedAdapter(AdapterCompanion companion) {
        super();
        this.companion = companion;
        setHasStableIds(true);
    }

    private static View view(int rLayout, ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(rLayout, parent, false);
    }

    public AdapterCompanion companion() {
        return companion;
    }

    @Override
    public long getItemId(int position) {
        return item(position).hashCode();
    }

    @NonNull
    @Override
    public DataViewHolder<Card> onCreateViewHolder(@NonNull ViewGroup parent, @Card.Type int viewType) {
        return switch (viewType) {
            case RESTO -> new RestoCardViewHolder(view(R.layout.home_card_resto, parent), this);
            case ACTIVITY -> new EventCardViewHolder(view(R.layout.home_card_event, parent), this);
            case SPECIAL_EVENT ->
                    new SpecialEventCardViewHolder(view(R.layout.home_card_special, parent), this.companion());
            case SCHAMPER -> new SchamperViewHolder(view(R.layout.home_card_schamper, parent), this);
            case NEWS_ITEM -> new NewsItemViewHolder(view(R.layout.home_card_news_item, parent), this);
            case URGENT_FM -> new UrgentViewHolder(view(R.layout.home_card_urgent, parent), this);
            case LIBRARY -> new LibraryViewHolder(view(R.layout.home_card_library, parent), this);
            case DEBUG -> new DebugCardViewHolder(view(R.layout.home_card_special, parent), this);
            default -> throw new IllegalArgumentException("Non-supported view type in home feed: " + viewType);
        };
    }

    @Override
    @Card.Type
    public int getItemViewType(int position) {
        return item(position).cardType();
    }

    public interface AdapterCompanion extends ResultStarter {

        ActivityHelper helper();

        void executeCommand(FeedCommand command);
    }
}
