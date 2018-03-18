package be.ugent.zeus.hydra.resto.extrafood;

import be.ugent.zeus.hydra.common.network.AbstractJsonRequestTest;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class ExtraFoodRequestTest extends AbstractJsonRequestTest<ExtraFood> {

    @Override
    protected String getRelativePath() {
        return "resto/extrafood.json";
    }

    @Override
    protected JsonOkHttpRequest<ExtraFood> getRequest() {
        return new ExtraFoodRequest(RuntimeEnvironment.application);
    }
}