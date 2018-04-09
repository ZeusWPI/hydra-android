package be.ugent.zeus.hydra.sko.lineup;

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
public class LineupRequestTest extends AbstractJsonRequestTest<List<Artist>> {

    @Override
    protected String getRelativePath() {
        return "sko_lineup.json";
    }

    @Override
    protected JsonOkHttpRequest<List<Artist>> getRequest() {
        return new LineupRequest(RuntimeEnvironment.application);
    }
}