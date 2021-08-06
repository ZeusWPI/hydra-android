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

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.feed.cards.implementations.AbstractFeedViewHolderTest;
import be.ugent.zeus.hydra.specialevent.SpecialEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.assertTextIs;
import static be.ugent.zeus.hydra.testing.RobolectricUtils.getShadowApplication;
import static be.ugent.zeus.hydra.testing.RobolectricUtils.inflate;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.assertEquals;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class SpecialEventCardViewHolderTest extends AbstractFeedViewHolderTest {

    @Test
    public void populate() {
        View view = inflate(activityContext, R.layout.home_card_special);
        SpecialEventCardViewHolder cardViewHolder = new SpecialEventCardViewHolder(view, adapter.getCompanion());
        SpecialEventCard card = generate(SpecialEventCard.class);
        SpecialEvent event = card.getSpecialEvent();
        event.setInApp("test");
        cardViewHolder.populate(card);

        assertTextIs(event.getName(), view.findViewById(R.id.title));
        assertTextIs(event.getSimpleText(), view.findViewById(R.id.text));

        view.performClick();
        Intent expected = new Intent(Intent.ACTION_VIEW, Uri.parse(event.getLink()));
        Intent actual = getShadowApplication().getNextStartedActivity();
        assertEquals(expected.getComponent(), actual.getComponent());
    }
}