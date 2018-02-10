package be.ugent.zeus.hydra.association.preference;

import android.os.Build;
import android.support.annotation.RequiresApi;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.TestApp;
import be.ugent.zeus.hydra.association.Association;
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
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
@Config(constants = BuildConfig.class, application = TestApp.class)
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