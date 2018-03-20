package be.ugent.zeus.hydra.resto.sandwich;

import be.ugent.zeus.hydra.common.network.AbstractJsonRequestTest;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class SandwichRequestTest extends AbstractJsonRequestTest<List<Sandwich>> {

    @Override
    protected String getRelativePath() {
        return "resto/sandwiches.json";
    }

    @Override
    protected JsonOkHttpRequest<List<Sandwich>> getRequest() {
        return new SandwichRequest(RuntimeEnvironment.application);
    }

    @Override
    protected List<Sandwich> getExpectedResult(String data) throws IOException {
        List<Sandwich> result = super.getExpectedResult(data);
        result.sort(Comparator.comparing(Sandwich::getName, String.CASE_INSENSITIVE_ORDER));
        return result;
    }
}