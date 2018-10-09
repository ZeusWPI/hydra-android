package be.ugent.zeus.hydra.sko.lineup;

import java.util.List;

import be.ugent.zeus.hydra.common.network.AbstractJsonRequestTest;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

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
        return new LineupRequest(context);
    }
}