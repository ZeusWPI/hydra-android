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

package be.ugent.zeus.hydra.feed.cards.implementations;

import android.content.Context;
import android.content.Intent;

import be.ugent.zeus.hydra.common.ui.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.feed.HomeFeedAdapter;
import be.ugent.zeus.hydra.testing.RobolectricUtils;
import org.junit.Before;
import org.mockito.stubbing.Answer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Niko Strijbol
 */
public class AbstractFeedViewHolderTest {

    protected HomeFeedAdapter adapter;
    protected ActivityHelper helper;
    protected Context activityContext;

    @Before
    public void setUp() {
        adapter = mock(HomeFeedAdapter.class);
        var companion = mock(HomeFeedAdapter.AdapterCompanion.class);
        when(adapter.companion()).thenReturn(companion);
        activityContext = RobolectricUtils.getActivityContext();
        when(companion.getContext()).thenReturn(activityContext);
        doAnswer((Answer<Void>) invocation -> {
            activityContext.startActivity(invocation.getArgument(0));
            return null;
        }).when(companion).startActivityForResult(any(Intent.class), anyInt());
        helper = mock(ActivityHelper.class);
        when(companion.helper()).thenReturn(helper);
    }
}