package be.ugent.zeus.hydra;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import be.ugent.zeus.hydra.common.RobolectricTest;
import be.ugent.zeus.hydra.common.database.Database;
import be.ugent.zeus.hydra.common.network.InstanceProvider;
import be.ugent.zeus.hydra.onboarding.OnboardingActivity;
import be.ugent.zeus.hydra.testing.NoNetworkInterceptor;
import jonathanfinerty.once.Once;
import okhttp3.OkHttpClient;
import org.apache.tools.ant.Main;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowApplication;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * TODO: this is ugly.
 *
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class MainActivityTest {

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        OkHttpClient.Builder builder = InstanceProvider.getBuilder(RuntimeEnvironment.application.getCacheDir());
        builder.addInterceptor(new NoNetworkInterceptor());
        Field field = InstanceProvider.class.getDeclaredField("client");
        field.setAccessible(true);
        field.set(null, builder.build());
    }

    @After
    public void cleanup() {
        Database.reset();
        InstanceProvider.reset();
    }

    @Test
    public void testStartOnboarding() {
        MainActivity activity = Robolectric.setupActivity(MainActivity.class);
        Intent expectedIntent = new Intent(activity, OnboardingActivity.class);
        Intent actualIntent = ShadowApplication.getInstance().getNextStartedActivity();
        assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());
    }

    @Test
    public void testNoOnboarding() {
        Once.markDone(MainActivity.ONCE_ONBOARDING);
        Robolectric.setupActivity(MainActivity.class);
        assertNull(ShadowApplication.getInstance().getNextStartedActivity());
    }

    @Test
    public void testOpenDrawer() {
        Once.markDone(MainActivity.ONCE_ONBOARDING);
        MainActivity activity = Robolectric.setupActivity(MainActivity.class);
        DrawerLayout drawer = activity.findViewById(R.id.drawer_layout);
        assertTrue(drawer.isDrawerOpen(Gravity.LEFT));
    }

    @Test
    public void testDontOpenDrawer() {
        Once.markDone(MainActivity.ONCE_ONBOARDING);
        Once.markDone(MainActivity.ONCE_DRAWER);
        MainActivity activity = Robolectric.setupActivity(MainActivity.class);
        DrawerLayout drawer = activity.findViewById(R.id.drawer_layout);
        assertFalse(drawer.isDrawerOpen(Gravity.LEFT));
    }
}