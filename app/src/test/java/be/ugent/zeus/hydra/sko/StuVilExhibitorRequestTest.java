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