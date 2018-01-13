package be.ugent.zeus.hydra.association.network;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.TestApp;
import be.ugent.zeus.hydra.association.Event;
import be.ugent.zeus.hydra.common.network.ArrayJsonSpringRequestTest;
import be.ugent.zeus.hydra.common.network.JsonSpringRequest;
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
public class EventRequestTest extends ArrayJsonSpringRequestTest<Event> {

    public EventRequestTest() {
        super(Event[].class);
    }

    @Override
    protected Resource getSuccessResponse() {
        return new ClassPathResource("all_activities.json");
    }

    @Override
    protected JsonSpringRequest<Event[]> getRequest() {
        return new EventRequest();
    }
}