package be.ugent.zeus.hydra.news;

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
public class UgentNewsRequestTest extends AbstractJsonRequestTest<List<UgentNewsArticle>> {

    @Override
    protected String getRelativePath() {
        return "ugent_news.json";
    }

    @Override
    protected JsonOkHttpRequest<List<UgentNewsArticle>> getRequest() {
        return new UgentNewsRequest(context);
    }

    @Override
    protected List<UgentNewsArticle> getExpectedResult(String data) throws IOException {
        List<UgentNewsArticle> unsorted = super.getExpectedResult(data);
        unsorted.sort(Comparator.comparing(UgentNewsArticle::getModified).reversed());
        return unsorted;
    }
}
