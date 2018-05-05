package be.ugent.zeus.hydra.minerva.auth;

import be.ugent.zeus.hydra.common.network.AbstractJsonRequestTest;
import be.ugent.zeus.hydra.common.request.RequestException;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.minerva.common.AuthDispatcher;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class UserInfoRequestTest extends AbstractJsonRequestTest<GrantInformation> {

    private static final String TEST_TOKEN = "test-token";

    @Override
    protected String getRelativePath() {
        return "minerva/userinfo.json";
    }

    @Override
    protected UserInfoRequest getRequest() {
        return new UserInfoRequest(RuntimeEnvironment.application, TEST_TOKEN);
    }

    @Test
    public void testTokenInjection() throws IOException, RequestException {
        MockWebServer server = new MockWebServer();
        server.setDispatcher(new AuthDispatcher(TEST_TOKEN, new MockResponse().setBody("{}")));
        server.start();

        UserInfoRequest request = getRequest();
        HttpUrl originalUrl = HttpUrl.parse(request.getAPIUrl());
        assertNotNull(originalUrl);
        HttpUrl serverUrl = server.url(originalUrl.encodedPath());
        request = spy(getRequest());
        doReturn(serverUrl.toString()).when(request).getAPIUrl();

        Result<GrantInformation> result = request.performRequest();

        if (result.hasException()) {
            throw result.getError();
        }

        assertTrue(result.hasData());
    }
}