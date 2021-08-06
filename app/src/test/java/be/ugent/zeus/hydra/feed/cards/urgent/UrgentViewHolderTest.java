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

package be.ugent.zeus.hydra.feed.cards.urgent;

import android.content.Intent;
import android.view.View;

import be.ugent.zeus.hydra.MainActivity;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.feed.cards.implementations.AbstractFeedViewHolderTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.getShadowApplication;
import static be.ugent.zeus.hydra.testing.RobolectricUtils.inflate;
import static org.junit.Assert.assertEquals;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class UrgentViewHolderTest extends AbstractFeedViewHolderTest {

    @Test
    public void populate() {
        View view = inflate(activityContext, R.layout.home_card_urgent);
        UrgentViewHolder viewHolder = new UrgentViewHolder(view, adapter);
        view.performClick();

        Intent expected = new Intent(view.getContext(), MainActivity.class);
        Intent actual = getShadowApplication().getNextStartedActivity();

        assertEquals(expected.getComponent(), actual.getComponent());
        assertEquals(R.id.drawer_urgent, actual.getIntExtra(MainActivity.ARG_TAB, -1));
    }
}