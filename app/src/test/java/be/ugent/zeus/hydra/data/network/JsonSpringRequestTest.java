package be.ugent.zeus.hydra.data.network;

import be.ugent.zeus.hydra.data.network.exceptions.RestTemplateException;
import be.ugent.zeus.hydra.repository.requests.Result;
import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStreamReader;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Abstract class containing some general tests that can be executed for every {@link be.ugent.zeus.hydra.data.network.JsonSpringRequest}.
 *
 * @param <R> The return type of the request.
 *
 * @author Niko Strijbol
 */
public abstract class JsonSpringRequestTest<R> {

    private final Class<R> clazz;

    public JsonSpringRequestTest(Class<R> clazz) {
        this.clazz = clazz;
    }

    /**
     * @return The resource that should be returned when the request is successful.
     */
    protected abstract Resource getSuccessResponse();

    /**
     * @return Instance of the Request that should be tested.
     */
    protected abstract JsonSpringRequest<R> getRequest();

    /**
     * By default this will call {@link Assert#assertEquals(Object, Object)} )}
     * @param expected The expected result.
     * @param actual The actual result.
     */
    protected void assertEquals(R expected, R actual) {
        Assert.assertEquals(expected, actual);
    }

    /**
     * By default this method will assume the resource is Json.
     * @param resource The resource, as given in {@link #getSuccessResponse()}.
     * @return The actual result.
     */
    protected R getExpectedResult(Resource resource) throws IOException {
        Gson gson = new Gson();
        return gson.fromJson(new InputStreamReader(resource.getInputStream()), clazz);
    }

    @Test
    public void testNormal() throws RestTemplateException, IOException {

        JsonSpringRequest<R> request = getRequest();
        Resource resource = getSuccessResponse();

        Result<R> result = getActualResult(request, resource);

        assertTrue(result.hasData());
        assertTrue(result.isDone());
        assertTrue(result.isWithoutError());

        // Test that all data is present by getting the normal data first
        R expected = getExpectedResult(resource);

        assertEquals(expected, result.getData());
    }

    @Test
    public void testError() throws RestTemplateException, IOException {

        JsonSpringRequest<R> request = getRequest();
        RestTemplate template = request.getRestTemplate();

        MockRestServiceServer mockServer = MockRestServiceServer.createServer(template);
        mockServer.expect(MockRestRequestMatchers.requestTo(request.getAPIUrl()))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withServerError());

        Result<R> result = request.performRequest(null);

        assertFalse(result.hasData());
        assertTrue(result.isDone());
        assertTrue(result.hasException());
    }

    protected Result<R> getActualResult(JsonSpringRequest<R> request, Resource from) throws RestTemplateException {
        MockRestServiceServer mockServer = MockRestServiceServer.createServer(request.getRestTemplate());
        mockServer.expect(MockRestRequestMatchers.requestTo(request.getAPIUrl()))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(from, MediaType.APPLICATION_JSON));

        return request.performRequest(null);
    }
}