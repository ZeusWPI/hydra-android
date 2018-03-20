package be.ugent.zeus.hydra.info;

import be.ugent.zeus.hydra.common.network.AbstractJsonRequestTest;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.List;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class InfoRequestTest extends AbstractJsonRequestTest<List<InfoItem>> {

    @Override
    protected String getRelativePath() {
        return "info_content.json";
    }

    @Override
    protected JsonOkHttpRequest<List<InfoItem>> getRequest() {
        return new InfoRequest(RuntimeEnvironment.application);
    }
}