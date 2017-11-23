package be.ugent.zeus.hydra.data.network.requests.association;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.domain.models.association.Association;
import be.ugent.zeus.hydra.data.network.ArrayJsonSpringRequestTest;
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
public class AssociationsRequestTest extends ArrayJsonSpringRequestTest<Association> {

    public AssociationsRequestTest() {
        super(Association[].class);
    }

    @Override
    protected Resource getSuccessResponse() {
        return new ClassPathResource("associations.json");
    }

    @Override
    protected JsonSpringRequest<Association[]> getRequest() {
        return new AssociationsRequest();
    }
}