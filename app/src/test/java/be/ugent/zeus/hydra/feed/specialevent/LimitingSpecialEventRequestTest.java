package be.ugent.zeus.hydra.feed.specialevent;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.TestApp;
import be.ugent.zeus.hydra.common.network.AbstractJsonSpringRequestTest;
import be.ugent.zeus.hydra.common.network.JsonSpringRequest;
import be.ugent.zeus.hydra.specialevent.SpecialEventRequest;
import be.ugent.zeus.hydra.specialevent.SpecialEventWrapper;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, application = TestApp.class)
public class LimitingSpecialEventRequestTest extends AbstractJsonSpringRequestTest<SpecialEventWrapper> {

    public LimitingSpecialEventRequestTest() {
        super(SpecialEventWrapper.class);
    }

    @Override
    protected Resource getSuccessResponse() {
        return new ClassPathResource("special_events.json");
    }

    @Override
    protected JsonSpringRequest<SpecialEventWrapper> getRequest() {
        return new SpecialEventRequest();
    }
}