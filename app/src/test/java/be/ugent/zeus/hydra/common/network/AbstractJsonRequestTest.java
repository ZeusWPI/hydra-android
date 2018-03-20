package be.ugent.zeus.hydra.common.network;

import be.ugent.zeus.hydra.common.request.Result;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.BufferedSource;
import okio.Okio;
import org.apache.commons.validator.routines.UrlValidator;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

/**
 * Base class for testing Json requests. This base class provides the test to see if the request parses the data
 * correctly.
 *
 * @author Niko Strijbol
 */
public abstract class AbstractJsonRequestTest<D> {

    protected Moshi moshi = InstanceProvider.getMoshi();

    protected final File getResourceFile(String resourcePath) {
        return new File(getClass().getClassLoader().getResource(resourcePath).getFile());
    }

    protected abstract String getRelativePath();

    /**
     * @return The request that should be tested.
     */
    protected abstract JsonOkHttpRequest<D> getRequest();

    protected D getExpectedResult(String data) throws IOException {
        JsonAdapter<D> adapter = moshi.adapter(getRequest().getTypeToken());
        return adapter.fromJson(data);
    }

    protected final String readData(File file) throws IOException {
        BufferedSource source = Okio.buffer(Okio.source(new FileInputStream(file)));
        return source.readUtf8();
    }

    @Test
    public void testValidUrl() {
        JsonOkHttpRequest<?> request = getRequest();
        UrlValidator validator = new UrlValidator();
        assertTrue(validator.isValid(request.getAPIUrl()));
    }

    @Test
    public void testNormal() throws IOException, JSONException {

        JsonOkHttpRequest<D> request = getRequest();
        File resource = getResourceFile(getRelativePath());
        String data = readData(resource);

        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.enqueue(new MockResponse()
                .setBody(data)
                .addHeader("Content-Type", "application/json; charset=utf-8")
        );
        mockWebServer.start();

        // Override the URL using Mockito. Not really pretty, but it is reliable and works.
        HttpUrl originalUrl = HttpUrl.parse(request.getAPIUrl());
        assertNotNull(originalUrl);
        HttpUrl serverUrl = mockWebServer.url(originalUrl.encodedPath());
        request = spyForNormal(request);
        doReturn(serverUrl.toString()).when(request).getAPIUrl();

        Result<D> result = request.performRequest(null);

        assertTrue(result.hasData());
        assertTrue(result.isDone());
        assertTrue(result.isWithoutError());

        // Test that all data is present by getting the normal data first.

        // Write the parsed data back to json and parse that.
        JsonAdapter<D> adapter = request.getAdapter().serializeNulls();
        String actualData = adapter.toJson(result.getData());

        System.out.println(actualData);
        //System.out.println(data);

        JSONAssert.assertEquals(data, actualData, false);

        mockWebServer.shutdown();
    }

    /**
     * Can also be used to inject spy methods.
     */
    protected JsonOkHttpRequest<D> spyForNormal(JsonOkHttpRequest<D> request) {
        return spy(request);
    }
}