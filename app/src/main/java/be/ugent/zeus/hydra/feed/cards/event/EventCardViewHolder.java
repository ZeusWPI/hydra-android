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

package be.ugent.zeus.hydra.feed.cards.event;

import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.association.event.Event;
import be.ugent.zeus.hydra.association.event.EventDetailsActivity;
import be.ugent.zeus.hydra.common.utils.DateUtils;
import be.ugent.zeus.hydra.feed.HomeFeedAdapter;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.CardViewHolder;
import be.ugent.zeus.hydra.feed.cards.PriorityUtils;
import be.ugent.zeus.hydra.feed.commands.DisableAssociationCommand;

/**
 * View holder for cards containing events.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class EventCardViewHolder extends CardViewHolder {

    private static final String TAG = "EventCardViewHolder";

    private final TextView start;
    private final TextView title;
    private final TextView association;
    private final ImageView imageView;

    private Pair<Event, Association> event;

    public EventCardViewHolder(View v, HomeFeedAdapter adapter) {
        super(v, adapter);
        title = v.findViewById(R.id.name);
        association = v.findViewById(R.id.association);
        start = v.findViewById(R.id.starttime);
        imageView = v.findViewById(R.id.imageView);
    }

    @Override
    public void populate(Card card) {
        super.populate(card);
        event = card.<EventCard>checkCard(Card.Type.ACTIVITY).getEvent();

        title.setText(event.first.getTitle());
        association.setText(event.first.getLocation());
        start.setText(DateUtils.relativeDateTimeString(event.first.getStart(), itemView.getContext(), false));
        String description = itemView.getResources().getString(R.string.feed_event_title);
        toolbar.setTitle(String.format(description, event.second.getAbbreviation()));

        PriorityUtils.loadThumbnail(itemView.getContext(), event.second.getImageLink(), imageView);

        itemView.setOnClickListener(v -> v.getContext().startActivity(EventDetailsActivity.start(itemView.getContext(), event.first, event.second)));
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (event == null) {
            Log.e(TAG, "The event was null when menu was called. Ignoring.");
            return super.onMenuItemClick(item);
        }
        if (item.getItemId() == R.id.menu_hide_association) {
            adapter.getCompanion().executeCommand(new DisableAssociationCommand(event.first.getAssociation()));
            return true;
        }
        return super.onMenuItemClick(item);
    }
}
