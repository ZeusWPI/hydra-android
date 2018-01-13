package be.ugent.zeus.hydra.sko;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.TestApp;
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
public class LineupRequestTest extends ArrayJsonSpringRequestTest<Artist> {

    public LineupRequestTest() {
        super(Artist[].class);
    }

    @Override
    protected Resource getSuccessResponse() {
        return new ClassPathResource("sko_lineup.json");
    }

    @Override
    protected JsonSpringRequest<Artist[]> getRequest() {
        return new LineupRequest();
    }
}