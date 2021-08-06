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

package be.ugent.zeus.hydra.association.list;

import android.content.Intent;
import android.view.View;

import java.time.LocalDate;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.association.event.Event;
import be.ugent.zeus.hydra.association.event.EventDetailsActivity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.*;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class EventViewHolderTest {

    private static void testEvent(boolean isLast) {
        View view = inflate(R.layout.item_event_item);
        EventViewHolder viewHolder = new EventViewHolder(view, new MemoryAssociationMap());
        EventItem item = new EventItem(generate(Event.class), isLast);
        Event event = item.getItem();
        viewHolder.populate(item);

        // We don't test all values, it's not worth it to just copy all code.
        assertTextIs(event.getTitle(), view.findViewById(R.id.name));
        assertNotEmpty(view.findViewById(R.id.starttime));

        // Check that the click listener works.
        View card = view.findViewById(R.id.card_view);
        card.performClick();

        Intent expectedIntent = EventDetailsActivity.start(card.getContext(), event, Association.unknown(event.getAssociation()));
        Intent actual = getShadowApplication().getNextStartedActivity();
        assertEquals(expectedIntent.getComponent(), actual.getComponent());
        assertNotNull(actual.getParcelableExtra(EventDetailsActivity.PARCEL_EVENT));

        int expected = isLast ? View.GONE : View.VISIBLE;
        assertEquals(expected, view.findViewById(R.id.item_event_divider).getVisibility());
    }

    @Test
    public void populateMiddleEvent() {
        testEvent(false);
    }

    @Test(expected = IllegalStateException.class)
    public void populateHeader() {
        View view = inflate(R.layout.item_event_item);
        EventViewHolder viewHolder = new EventViewHolder(view, new MemoryAssociationMap());
        EventItem item = new EventItem(generate(LocalDate.class));
        viewHolder.populate(item);
    }

    @Test
    public void populateLastEvent() {
        testEvent(true);
    }

}
