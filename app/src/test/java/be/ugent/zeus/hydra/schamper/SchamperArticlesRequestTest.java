package be.ugent.zeus.hydra.schamper;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.TestApp;
import be.ugent.zeus.hydra.schamper.Article;
import be.ugent.zeus.hydra.common.network.ArrayJsonSpringRequestTest;
import be.ugent.zeus.hydra.common.network.JsonSpringRequest;
import be.ugent.zeus.hydra.schamper.SchamperArticlesRequest;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, application = TestApp.class)
public class SchamperArticlesRequestTest extends ArrayJsonSpringRequestTest<Article> {

    public SchamperArticlesRequestTest() {
        super(Article[].class);
    }

    @Override
    protected Resource getSuccessResponse() {
        return new ClassPathResource("daily_android.json");
    }

    @Override
    protected JsonSpringRequest<Article[]> getRequest() {
        return new SchamperArticlesRequest();
    }
}