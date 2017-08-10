package be.ugent.zeus.hydra.data.network.requests.sko;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.data.models.sko.Exhibitor;
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
public class StuVilExhibitorRequestTest extends ArrayJsonSpringRequestTest<Exhibitor> {

    public StuVilExhibitorRequestTest() {
        super(Exhibitor[].class);
    }

    @Override
    protected Resource getSuccessResponse() {
        return new ClassPathResource("sko_student_village.json");
    }

    @Override
    protected JsonSpringRequest<Exhibitor[]> getRequest() {
        return new StuVilExhibitorRequest();
    }
}