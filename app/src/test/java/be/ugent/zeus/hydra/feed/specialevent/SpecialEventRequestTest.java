package be.ugent.zeus.hydra.feed.specialevent;

import be.ugent.zeus.hydra.common.network.AbstractJsonRequestTest;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import be.ugent.zeus.hydra.specialevent.SpecialEventRequest;
import be.ugent.zeus.hydra.specialevent.SpecialEventWrapper;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class SpecialEventRequestTest extends AbstractJsonRequestTest<SpecialEventWrapper> {

    @Override
    protected String getRelativePath() {
        return "special_events.json";
    }

    @Override
    protected JsonOkHttpRequest<SpecialEventWrapper> getRequest() {
        return new SpecialEventRequest(RuntimeEnvironment.application);
    }
}