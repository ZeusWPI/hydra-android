package be.ugent.zeus.hydra.data.sync.course;

import android.content.Intent;
import android.os.IBinder;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ServiceTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeoutException;

import static junit.framework.TestCase.assertNotNull;

/**
 * Test that the sync service works correctly.
 *
 * @author Niko Strijbol
 */
@RunWith(AndroidJUnit4.class)
public class CourseSyncTest {

    @Rule
    public final ServiceTestRule serviceTestRule = new ServiceTestRule();

    @Test
    public void testService() throws TimeoutException {
        Intent serviceIntent = new Intent(InstrumentationRegistry.getTargetContext(), CourseService.class);
        IBinder binder = serviceTestRule.bindService(serviceIntent);
        assertNotNull(binder);
    }
}