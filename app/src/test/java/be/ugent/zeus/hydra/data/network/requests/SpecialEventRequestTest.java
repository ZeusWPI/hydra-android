package be.ugent.zeus.hydra.data.network.requests;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.data.models.specialevent.SpecialEventWrapper;
import be.ugent.zeus.hydra.data.network.AbstractJsonSpringRequestTest;
import be.ugent.zeus.hydra.data.network.JsonSpringRequest;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class SpecialEventRequestTest extends AbstractJsonSpringRequestTest<SpecialEventWrapper> {

    public SpecialEventRequestTest() {
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