package be.ugent.zeus.hydra.resto.meta;

import be.ugent.zeus.hydra.common.network.AbstractJsonRequestTest;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class MetaRequestTest extends AbstractJsonRequestTest<RestoMeta> {

    @Override
    protected String getRelativePath() {
        return "resto/meta.json";
    }

    @Override
    protected JsonOkHttpRequest<RestoMeta> getRequest() {
        return new MetaRequest(RuntimeEnvironment.application);
    }
}