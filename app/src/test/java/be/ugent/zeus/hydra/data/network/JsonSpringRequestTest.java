package be.ugent.zeus.hydra.data.network;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.TestApp;
import be.ugent.zeus.hydra.data.network.exceptions.RestTemplateException;
import be.ugent.zeus.hydra.repository.requests.Result;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, application = TestApp.class)
public class JsonSpringRequestTest {

    @Test
    public void testError() throws RestTemplateException {

        JsonSpringRequest<?> request = TestRequest.any();
        RestTemplate template = request.getRestTemplate();

        MockRestServiceServer mockServer = MockRestServiceServer.createServer(template);
        mockServer.expect(MockRestRequestMatchers.requestTo(request.getAPIUrl()))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withServerError());

        Result<?> result = request.performRequest(null);

        assertFalse(result.hasData());
        assertTrue(result.isDone());
        assertTrue(result.hasException());
    }

    @Test
    public void testEmptyBody() throws RestTemplateException {

        JsonSpringRequest<?> request = TestRequest.any();
        RestTemplate template = request.getRestTemplate();

        MockRestServiceServer mockServer = MockRestServiceServer.createServer(template);
        mockServer.expect(MockRestRequestMatchers.requestTo(request.getAPIUrl()))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(new byte[0], MediaType.APPLICATION_JSON));

        Result<?> result = request.performRequest(null);

        assertFalse(result.hasData());
        assertTrue(result.isDone());
        assertTrue(result.hasException());
    }

    @Test
    public void testWrongFormat() throws RestTemplateException {

        JsonSpringRequest<Integer> request = TestRequest.forType(Integer.class);
        RestTemplate template = request.getRestTemplate();

        MockRestServiceServer mockServer = MockRestServiceServer.createServer(template);
        mockServer.expect(MockRestRequestMatchers.requestTo(request.getAPIUrl()))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess("THIS IS NO INT", MediaType.APPLICATION_JSON));

        Result<Integer> result = request.performRequest(null);

        assertFalse(result.hasData());
        assertTrue(result.isDone());
        assertTrue(result.hasException());
    }

    /**
     * This tests that the class handles {@link JsonSpringRequest#createRestTemplate()} correctly when this method
     * throws an error.
     */
    @Test
    public void testRestTemplateError() {
        JsonSpringRequest<?> request = TestRequest.problematicRestTemplate();
        Result<?> result = request.performRequest(null);
        assertFalse(result.hasData());
        assertTrue(result.isDone());
        assertTrue(result.hasException());
    }
}
