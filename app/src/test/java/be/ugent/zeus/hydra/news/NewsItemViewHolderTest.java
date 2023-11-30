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

package be.ugent.zeus.hydra.news;

import android.net.Uri;
import android.view.View;

import java.time.OffsetDateTime;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.customtabs.ActivityHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.assertNotEmpty;
import static be.ugent.zeus.hydra.testing.RobolectricUtils.assertTextIs;
import static be.ugent.zeus.hydra.testing.RobolectricUtils.inflate;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class NewsItemViewHolderTest {

    private ActivityHelper helper;

    @Before
    public void setUp() {
        helper = mock(ActivityHelper.class);
    }

    @Test
    public void populate() {
        View view = inflate(R.layout.item_news);
        NewsItemViewHolder viewHolder = new NewsItemViewHolder(view, helper);
        NewsArticle newsItem = generate(NewsArticle.class);
        viewHolder.populate(newsItem);

        assertTextIs(newsItem.title(), view.findViewById(R.id.name));
        assertNotEmpty(view.findViewById(R.id.info));
        assertNotEmpty(view.findViewById(R.id.article_excerpt));

        assertTrue(view.hasOnClickListeners());

        view.performClick();
        verify(helper, times(1)).openCustomTab(any(Uri.class));
    }

    @Test
    public void populateVariant() {
        View view = inflate(R.layout.item_news);
        NewsItemViewHolder viewHolder = new NewsItemViewHolder(view, helper);
        NewsArticle newsItem = generate(NewsArticle.class, "summary")
                .withUpdated(OffsetDateTime.now().plusDays(10));
        viewHolder.populate(newsItem);

        assertTextIs(newsItem.title(), view.findViewById(R.id.name));
        assertNotEmpty(view.findViewById(R.id.info));
        assertNotEmpty(view.findViewById(R.id.article_excerpt));

        assertTrue(view.hasOnClickListeners());
    }
}
