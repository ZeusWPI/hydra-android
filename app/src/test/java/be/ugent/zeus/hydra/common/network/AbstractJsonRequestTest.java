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
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

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

    /**
     * By default this will call {@link Assert#assertEquals(Object, Object)} )}
     *
     * @param expected The expected result.
     * @param actual   The actual result.
     */
    protected void assertEquals(D expected, D actual) {
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testValidUrl() {
        JsonOkHttpRequest<?> request = getRequest();
        UrlValidator validator = new UrlValidator();
        assertTrue(validator.isValid(request.getAPIUrl()));
    }

    @Test
    public void testNormal() throws IOException {

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
        request = spy(request);
        when(request.getAPIUrl()).thenReturn(serverUrl.toString());

        Result<D> result = request.performRequest(null);

        assertTrue(result.hasData());
        assertTrue(result.isDone());
        assertTrue(result.isWithoutError());

        // Test that all data is present by getting the normal data first
        D expected = getExpectedResult(data);

        assertEquals(expected, result.getData());

        mockWebServer.shutdown();
    }
}