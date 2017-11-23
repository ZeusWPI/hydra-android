package be.ugent.zeus.hydra.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.TestApp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, application = TestApp.class)
public class ColourUtilsTest {

    @Test
    public void isDark() {
        Context context = RuntimeEnvironment.application;
        // Rather simple test to test basics of this simple code.
        assertTrue(ColourUtils.isDark(ContextCompat.getColor(context, android.R.color.black)));
        assertFalse(ColourUtils.isDark(ContextCompat.getColor(context, android.R.color.white)));
    }
}