package be.ugent.zeus.hydra.association.news;

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
public class UgentNewsRequestTest extends AbstractJsonRequestTest<List<UgentNewsItem>> {

    @Override
    protected String getRelativePath() {
        return "ugent_news.json";
    }

    @Override
    protected JsonOkHttpRequest<List<UgentNewsItem>> getRequest() {
        return new UgentNewsRequest(context);
    }

    @Override
    protected List<UgentNewsItem> getExpectedResult(String data) throws IOException {
        List<UgentNewsItem> unsorted = super.getExpectedResult(data);
        unsorted.sort(Comparator.comparing(UgentNewsItem::getModified).reversed());
        return unsorted;
    }
}