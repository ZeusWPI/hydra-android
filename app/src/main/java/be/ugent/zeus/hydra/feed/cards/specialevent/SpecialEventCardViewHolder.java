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

package be.ugent.zeus.hydra.feed.cards.specialevent;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.common.utils.NetworkUtils;
import be.ugent.zeus.hydra.feed.HomeFeedAdapter;
import be.ugent.zeus.hydra.feed.SwipeDismissableViewHolder;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.commands.DisableIndividualCard;
import be.ugent.zeus.hydra.specialevent.SpecialEvent;
import com.squareup.picasso.Picasso;

/**
 * Home feed view holder for special events.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class SpecialEventCardViewHolder extends DataViewHolder<Card> implements SwipeDismissableViewHolder {

    private final TextView title;
    private final TextView text;
    private final ImageView image;
    private final HomeFeedAdapter.AdapterCompanion companion;

    private SpecialEvent event;
    private Card card;

    public SpecialEventCardViewHolder(View itemView, HomeFeedAdapter.AdapterCompanion companion) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        text = itemView.findViewById(R.id.text);
        image = itemView.findViewById(R.id.image);
        this.companion = companion;
    }

    @Override
    public void populate(Card card) {

        SpecialEventCard eventCard = card.checkCard(Card.Type.SPECIAL_EVENT);
        event = eventCard.getSpecialEvent();

        title.setText(event.getName());
        text.setText(event.getSimpleText());
        Picasso.get().load(event.getImage()).into(image);

        itemView.setOnClickListener(v -> NetworkUtils.maybeLaunchIntent(v.getContext(), event.getViewIntent(v.getContext())));

        this.card = card;
    }

    @Override
    public void onSwiped() {
        // Do nothing for now!
        if (event != null && card != null) {
            companion.executeCommand(new DisableIndividualCard(card));
        }
    }

    @Override
    public boolean isSwipeEnabled() {
        return true;
    }
}
