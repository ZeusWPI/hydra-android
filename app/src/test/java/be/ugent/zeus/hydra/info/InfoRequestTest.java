package be.ugent.zeus.hydra.info;

import java.util.List;

import be.ugent.zeus.hydra.common.network.AbstractJsonRequestTest;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

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
        return new InfoRequest(context);
    }
}