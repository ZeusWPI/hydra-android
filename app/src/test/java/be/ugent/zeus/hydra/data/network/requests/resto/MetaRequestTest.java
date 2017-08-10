package be.ugent.zeus.hydra.data.network.requests.resto;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.data.models.resto.RestoMeta;
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
public class MetaRequestTest extends AbstractJsonSpringRequestTest<RestoMeta> {

    public MetaRequestTest() {
        super(RestoMeta.class);
    }

    @Override
    protected Resource getSuccessResponse() {
        return new ClassPathResource("resto_meta.json");
    }

    @Override
    protected JsonSpringRequest<RestoMeta> getRequest() {
        return new MetaRequest();
    }
}