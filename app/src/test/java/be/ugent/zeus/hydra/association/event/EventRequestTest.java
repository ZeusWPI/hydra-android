package be.ugent.zeus.hydra.association.event;

import be.ugent.zeus.hydra.association.list.Filter;
import be.ugent.zeus.hydra.common.network.AbstractJsonRequestTest;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class EventRequestTest extends AbstractJsonRequestTest<EventList> {

    @Override
    protected String getRelativePath() {
        return "activiteiten.json";
    }

    @Override
    protected JsonOkHttpRequest<EventList> getRequest() {
        return new RawEventRequest(context, new Filter());
    }
}