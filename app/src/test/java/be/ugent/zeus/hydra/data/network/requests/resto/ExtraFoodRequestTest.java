package be.ugent.zeus.hydra.data.network.requests.resto;

import android.os.Build;
import android.support.annotation.RequiresApi;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.TestApp;
import be.ugent.zeus.hydra.data.network.AbstractJsonSpringRequestTest;
import be.ugent.zeus.hydra.data.network.JsonSpringRequest;
import be.ugent.zeus.hydra.domain.models.resto.ExtraFood;
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
@RequiresApi(api = Build.VERSION_CODES.N)
public class ExtraFoodRequestTest extends AbstractJsonSpringRequestTest<ExtraFood> {

    public ExtraFoodRequestTest() {
        super(ExtraFood.class);
    }

    @Override
    protected Resource getSuccessResponse() {
        return new ClassPathResource("resto/extrafood.json");
    }

    @Override
    protected JsonSpringRequest<ExtraFood> getRequest() {
        return new ExtraFoodRequest();
    }
}