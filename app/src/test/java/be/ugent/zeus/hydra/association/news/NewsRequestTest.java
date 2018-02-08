package be.ugent.zeus.hydra.association.news;

import android.os.Build;
import android.support.annotation.RequiresApi;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.TestApp;
import be.ugent.zeus.hydra.association.news.NewsItem;
import be.ugent.zeus.hydra.association.news.NewsRequest;
import be.ugent.zeus.hydra.common.network.ArrayJsonSpringRequestTest;
import be.ugent.zeus.hydra.common.network.JsonSpringRequest;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, application = TestApp.class)
@RequiresApi(api = Build.VERSION_CODES.N)
public class NewsRequestTest extends ArrayJsonSpringRequestTest<NewsItem> {

    public NewsRequestTest() {
        super(NewsItem[].class);
    }

    @Override
    protected Resource getSuccessResponse() {
        return new ClassPathResource("old_news.json");
    }

    @Override
    protected JsonSpringRequest<NewsItem[]> getRequest() {
        return new NewsRequest();
    }

    @Override
    protected NewsItem[] getExpectedResult(Resource resource) throws IOException {
        NewsItem[] result = super.getExpectedResult(resource);
        Arrays.sort(result, Comparator.comparing(NewsItem::getDate).reversed());
        return result;
    }
}