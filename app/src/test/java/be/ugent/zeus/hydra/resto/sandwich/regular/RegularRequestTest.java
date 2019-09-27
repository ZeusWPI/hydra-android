package be.ugent.zeus.hydra.resto.sandwich.regular;

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
public class RegularRequestTest extends AbstractJsonRequestTest<List<RegularSandwich>> {

    @Override
    protected String getRelativePath() {
        return "resto/sandwiches.json";
    }

    @Override
    protected JsonOkHttpRequest<List<RegularSandwich>> getRequest() {
        return new RegularRequest(context);
    }

    @Override
    protected List<RegularSandwich> getExpectedResult(String data) throws IOException {
        List<RegularSandwich> result = super.getExpectedResult(data);
        result.sort(Comparator.comparing(RegularSandwich::getName, String.CASE_INSENSITIVE_ORDER));
        return result;
    }
}
