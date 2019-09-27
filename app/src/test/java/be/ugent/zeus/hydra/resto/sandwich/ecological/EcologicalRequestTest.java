package be.ugent.zeus.hydra.resto.sandwich.ecological;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import be.ugent.zeus.hydra.common.network.AbstractJsonRequestTest;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class EcologicalRequestTest extends AbstractJsonRequestTest<List<EcologicalSandwich>> {

    @Override
    protected String getRelativePath() {
        return "resto/ecological.json";
    }

    @Override
    protected JsonOkHttpRequest<List<EcologicalSandwich>> getRequest() {
        return new EcologicalRequest(context);
    }

    @Override
    protected List<EcologicalSandwich> getExpectedResult(String data) throws IOException {
        List<EcologicalSandwich> result = super.getExpectedResult(data);
        result.sort(Comparator.comparing(EcologicalSandwich::getStart));
        return result;
    }
}
