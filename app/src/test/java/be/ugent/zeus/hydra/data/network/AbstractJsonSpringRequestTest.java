package be.ugent.zeus.hydra.data.network;

import android.os.Build;
import android.support.annotation.RequiresApi;

import be.ugent.zeus.hydra.data.network.exceptions.RestTemplateException;
import be.ugent.zeus.hydra.repository.requests.Result;
import com.google.gson.Gson;
import org.apache.commons.validator.routines.UrlValidator;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static junit.framework.TestCase.assertTrue;

/**
 * Abstract class containing some general tests that can be executed for every {@link be.ugent.zeus.hydra.data.network.JsonSpringRequest}.
 *
 * @param <R> The return type of the request.
 *
 * @author Niko Strijbol
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public abstract class AbstractJsonSpringRequestTest<R> {

    private final Class<R> clazz;

    public AbstractJsonSpringRequestTest(Class<R> clazz) {
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
        return gson.fromJson(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8), clazz);
    }

    @Test
    public void testNormal() throws RestTemplateException, IOException {

        JsonSpringRequest<R> request = getRequest();
        Resource resource = getSuccessResponse();

        MockRestServiceServer mockServer = MockRestServiceServer.createServer(request.getRestTemplate());
        mockServer.expect(MockRestRequestMatchers.requestTo(request.getAPIUrl()))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(resource, MediaType.APPLICATION_JSON));

        Result<R> result = request.performRequest(null);

        assertTrue(result.hasData());
        assertTrue(result.isDone());
        assertTrue(result.isWithoutError());

        // Test that all data is present by getting the normal data first
        R expected = getExpectedResult(resource);

        assertEquals(expected, result.getData());
    }

    @Test
    public void testValidUrl() {
        JsonSpringRequest<?> request = getRequest();
        UrlValidator validator = new UrlValidator();
        assertTrue(validator.isValid(request.getAPIUrl()));
    }
}