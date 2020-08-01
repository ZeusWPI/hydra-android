package be.ugent.zeus.hydra.association.event;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;

import be.ugent.zeus.hydra.common.network.InstanceProvider;
import be.ugent.zeus.hydra.testing.NoNetworkInterceptor;
import be.ugent.zeus.hydra.testing.RobolectricUtils;
import be.ugent.zeus.hydra.testing.Utils;
import okhttp3.OkHttpClient;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;

/**
 * @author Niko Strijbol
 */
@Ignore("Race conditions make these tests unreliable")
@RunWith(RobolectricTestRunner.class)
public class EventDetailsActivityTest {

    @Before
    public void setUp() {
        OkHttpClient.Builder builder = InstanceProvider.getBuilder(ApplicationProvider.getApplicationContext().getCacheDir());
        builder.addInterceptor(new NoNetworkInterceptor());
        InstanceProvider.setClient(builder.build());
        Intents.init();
    }

    @Test
    public void shouldReturnCorrectIntent_whenStartIsCalled() {
        Event e = Utils.generate(Event.class);
        Intent actual = EventDetailsActivity.start(RobolectricUtils.getActivityContext(), e);
        Intent expected = new Intent(RobolectricUtils.getActivityContext(), EventDetailsActivity.class);

        assertEquals(expected.getComponent(), actual.getComponent());
        assertEquals(e, actual.getParcelableExtra(EventDetailsActivity.PARCEL_EVENT));
    }
}
