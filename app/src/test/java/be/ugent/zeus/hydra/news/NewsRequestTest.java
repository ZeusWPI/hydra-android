package be.ugent.zeus.hydra.news;

import be.ugent.zeus.hydra.common.network.AbstractJsonRequestTest;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class NewsRequestTest extends AbstractJsonRequestTest<NewsStream> {

    @Override
    protected String getRelativePath() {
        return "ugent_news.json";
    }

    @Override
    protected JsonOkHttpRequest<NewsStream> getRequest() {
        return new NewsRequest(context);
    }
}
