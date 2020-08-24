package be.ugent.zeus.hydra.association.event;

import java.util.List;

import be.ugent.zeus.hydra.common.network.AbstractJsonRequestTest;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

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
        return new RawEventRequest(context, "");
    }
}