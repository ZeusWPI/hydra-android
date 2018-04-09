package be.ugent.zeus.hydra.association.event;

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
public class EventRequestTest extends AbstractJsonRequestTest<List<Event>> {

    @Override
    protected String getRelativePath() {
        return "all_activities.json";
    }

    @Override
    protected JsonOkHttpRequest<List<Event>> getRequest() {
        return new RawEventRequest(RuntimeEnvironment.application);
    }
}