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
