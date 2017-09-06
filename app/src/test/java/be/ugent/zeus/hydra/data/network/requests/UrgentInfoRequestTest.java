package be.ugent.zeus.hydra.data.network.requests;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.data.models.UrgentInfo;
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
public class UrgentInfoRequestTest extends AbstractJsonSpringRequestTest<UrgentInfo> {

    public UrgentInfoRequestTest() {
        super(UrgentInfo.class);
    }

    @Override
    protected Resource getSuccessResponse() {
        return new ClassPathResource("urgent.json");
    }

    @Override
    protected JsonSpringRequest<UrgentInfo> getRequest() {
        return new UrgentInfoRequest();
    }
}