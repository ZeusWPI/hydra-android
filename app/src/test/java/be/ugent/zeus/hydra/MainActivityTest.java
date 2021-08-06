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

package be.ugent.zeus.hydra;

import android.content.ComponentName;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import be.ugent.zeus.hydra.common.database.Database;
import be.ugent.zeus.hydra.common.network.InstanceProvider;
import be.ugent.zeus.hydra.onboarding.OnboardingActivity;
import be.ugent.zeus.hydra.testing.NoNetworkInterceptor;
import jonathanfinerty.once.Once;
import okhttp3.OkHttpClient;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.annotation.LooperMode;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.contrib.DrawerMatchers.isOpen;
import static androidx.test.espresso.intent.Intents.getIntents;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;
import static org.robolectric.annotation.LooperMode.Mode.PAUSED;

/**
 * @author Niko Strijbol
 */
@Ignore("Race conditions make these tests unreliable")
@LooperMode(PAUSED)
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Before
    public void setUp() {
        OkHttpClient.Builder builder = InstanceProvider.getBuilder(ApplicationProvider.getApplicationContext().getCacheDir());
        builder.addInterceptor(new NoNetworkInterceptor());
        InstanceProvider.setClient(builder.build());
        Intents.init();
    }

    @After
    public void cleanup() {
        Database.reset();
        InstanceProvider.reset();
        Intents.release();
    }

    @Test
    public void shouldOpenOnboarding_WhenFirstUsed() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(a -> intended(hasComponent(new ComponentName(a, OnboardingActivity.class))));
        }
    }

    @Test
    public void shouldNotOpenOnboarding_WhenPreviouslyUsed() {
        Once.markDone(MainActivity.ONCE_ONBOARDING);
        try (ActivityScenario<MainActivity> ignored = ActivityScenario.launch(MainActivity.class)) {
            assertTrue(getIntents().isEmpty());
        }
    }

    @Test
    public void shouldHaveOpenDrawer_WhenFirstUsed() {
        Once.markDone(MainActivity.ONCE_ONBOARDING);
        try (ActivityScenario<MainActivity> ignored = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
        }
    }

    @Test
    public void shouldNotHaveOpenDrawer_WhenPreviouslyUsed() {
        Once.markDone(MainActivity.ONCE_ONBOARDING);
        Once.markDone(MainActivity.ONCE_DRAWER);
        try (ActivityScenario<MainActivity> ignored = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.drawer_layout)).check(matches(isClosed()));
        }
    }
}