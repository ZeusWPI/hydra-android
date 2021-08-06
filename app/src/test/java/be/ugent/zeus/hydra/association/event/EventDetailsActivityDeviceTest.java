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

package be.ugent.zeus.hydra.association.event;

import android.content.Context;
import android.content.Intent;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.common.network.InstanceProvider;
import be.ugent.zeus.hydra.testing.NoNetworkInterceptor;
import be.ugent.zeus.hydra.testing.Utils;
import okhttp3.OkHttpClient;
import org.junit.*;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * @author Niko Strijbol
 */
@Ignore("Race conditions make these tests unreliable")
@RunWith(AndroidJUnit4.class)
public class EventDetailsActivityDeviceTest {

    private final Event event = Utils.generate(Event.class);
    private final Association association = Utils.generate(Association.class);

    @Rule
    public ActivityTestRule<EventDetailsActivity> rule = new ActivityTestRule<EventDetailsActivity>(EventDetailsActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            return EventDetailsActivity.start(targetContext, event, association);
        }
    };

    @Before
    public void setUp() {
        OkHttpClient.Builder builder = InstanceProvider.getBuilder(ApplicationProvider.getApplicationContext().getCacheDir());
        builder.addInterceptor(new NoNetworkInterceptor());
        InstanceProvider.setClient(builder.build());
        Intents.init();
    }

    @After
    public void cleanup() {
        InstanceProvider.reset();
        Intents.release();
    }

    @Test
    public void shouldDisplayTitle() {
        onView(withId(R.id.title))
                .check(matches(isDisplayed()))
                .check(matches(withText(event.getTitle())));
    }

    @Test
    public void shouldDisplayAssociationData() {
        onView(withId(R.id.event_organisator_main))
                .check(matches(isDisplayed()))
                .check(matches(withText(association.getName())));
    }

    @Test
    public void shouldDisplayDescription() {
        onView(withId(R.id.description))
                .check(matches(withText(event.getDescription())));
    }
}
