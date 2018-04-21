package be.ugent.zeus.hydra.urgent;

import be.ugent.zeus.hydra.common.network.AbstractJsonRequestTest;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class UrgentInfoRequestTest extends AbstractJsonRequestTest<UrgentInfo> {

    @Override
    protected String getRelativePath() {
        return "urgent.json";
    }

    @Override
    protected JsonOkHttpRequest<UrgentInfo> getRequest() {
        return new UrgentInfoRequest(RuntimeEnvironment.application);
    }
}