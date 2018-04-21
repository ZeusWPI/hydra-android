package be.ugent.zeus.hydra.sko.timeline;

import be.ugent.zeus.hydra.common.network.AbstractJsonRequestTest;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.List;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class TimelineRequestTest extends AbstractJsonRequestTest<List<TimelinePost>> {

    @Override
    protected String getRelativePath() {
        return "sko_timeline.json";
    }

    @Override
    protected JsonOkHttpRequest<List<TimelinePost>> getRequest() {
        return new TimelineRequest(RuntimeEnvironment.application);
    }
}