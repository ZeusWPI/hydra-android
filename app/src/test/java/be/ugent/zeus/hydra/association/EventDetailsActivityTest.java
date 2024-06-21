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

package be.ugent.zeus.hydra.association;

import android.content.Intent;
import androidx.core.content.IntentCompat;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;

import be.ugent.zeus.hydra.common.network.InstanceProvider;
import be.ugent.zeus.hydra.testing.NoNetworkInterceptor;
import be.ugent.zeus.hydra.testing.Utils;
import okhttp3.OkHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class EventDetailsActivityTest {

    @Before
    public void setUp() {
        OkHttpClient.Builder builder = InstanceProvider.builder(ApplicationProvider.getApplicationContext().getCacheDir());
        builder.addInterceptor(new NoNetworkInterceptor());
        InstanceProvider.client(builder.build());
        Intents.init();
    }

    @Test
    public void shouldReturnCorrectIntent_whenStartIsCalled() {
        Event e = Utils.generate(Event.class);
        Association a = Utils.generate(Association.class);

        var aContext = getInstrumentation().getTargetContext();
        var actual = EventDetailsActivity.start(aContext, e, a);
        var expected = new Intent(aContext, EventDetailsActivity.class);

        assertEquals(expected.getComponent(), actual.getComponent());
        assertEquals(e, IntentCompat.getParcelableExtra(actual, EventDetailsActivity.PARCEL_EVENT, Event.class));
    }
}
