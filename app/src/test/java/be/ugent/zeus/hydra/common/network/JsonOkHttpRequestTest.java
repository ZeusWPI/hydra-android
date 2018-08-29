package be.ugent.zeus.hydra.common.network;

import android.os.Bundle;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.common.arch.data.BaseLiveData;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.RequestException;
import be.ugent.zeus.hydra.common.request.Result;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.threeten.bp.Duration;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Test the default request. Includes tests for caching and offline handling. Therefor, when testing implementations of
 * this class, it is often enough to test the "everything OK" case, as other cases are covered by this test.
 *
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class JsonOkHttpRequestTest {

    private MockWebServer server;

    @Before
    public void setUp() {
        // TODO: this is ugly but necessary due to the singletons
        InstanceProvider.reset();
        server = new MockWebServer();
    }

    @After
    public void breakDown() throws IOException {
        server.shutdown();
    }

    @Test
    public void testFromNetworkFine() throws IOException {
        server.enqueue(integerJsonResponse());
        server.start();

        HttpUrl url = server.url("/fine.json");

        Request<Integer> request = new TestRequest(url);
        Result<Integer> result = request.execute();

        assertTrue(result.hasData());
        assertEquals(1, (int) result.getData());
    }

    @Test
    public void testFromCacheFine() throws IOException {
        server.enqueue(integerJsonResponse());
        server.enqueue(integerJsonResponse());
        server.start();
        HttpUrl url = server.url("/fine.json");

        Request<Integer> request = new TestRequest(url);
        Result<Integer> result = request.execute();
        assertTrue(result.hasData());
        assertEquals(1, (int) result.getData());

        Result<Integer> result2 = request.execute();
        assertTrue(result2.hasData());
        assertEquals(1, (int) result2.getData());

        Cache cache = InstanceProvider.getClient(RuntimeEnvironment.application).cache();
        assertEquals(1, cache.networkCount());
        assertEquals(1, cache.hitCount());
        assertEquals(2, cache.requestCount());
    }

    @Test
    public void testFromCacheOverride() throws IOException {
        server.enqueue(integerJsonResponse());
        server.enqueue(integerJsonResponse());
        server.start();
        HttpUrl url = server.url("/fine.json");

        Request<Integer> request = new TestRequest(url);
        Result<Integer> result = request.execute();
        assertTrue(result.hasData());
        assertEquals(1, (int) result.getData());

        Bundle args = new Bundle();
        args.putBoolean(BaseLiveData.REFRESH_COLD, true);
        Result<Integer> result2 = request.execute(args);
        assertTrue(result2.hasData());
        assertEquals(1, (int) result2.getData());

        Cache cache = InstanceProvider.getClient(RuntimeEnvironment.application).cache();
        assertEquals(2, cache.networkCount());
        assertEquals(0, cache.hitCount());
        assertEquals(2, cache.requestCount());
    }

    @Test
    public void testZeroCacheRequest() throws IOException {
        server.enqueue(integerJsonResponse());
        server.enqueue(integerJsonResponse());
        server.start();
        HttpUrl url = server.url("/fine.json");

        Request<Integer> request = new TestRequest(url, Duration.ZERO);
        Result<Integer> result = request.execute();
        assertTrue(result.hasData());
        assertEquals(1, (int) result.getData());

        Result<Integer> result2 = request.execute();
        assertTrue(result2.hasData());
        assertEquals(1, (int) result2.getData());

        Cache cache = InstanceProvider.getClient(RuntimeEnvironment.application).cache();
        assertEquals(2, cache.networkCount());
        assertEquals(0, cache.hitCount());
        assertEquals(2, cache.requestCount());
    }

    @Test(expected = RequestException.class)
    public void testWrongFormatEmpty() throws IOException, RequestException {
        server.enqueue(new MockResponse());
        server.start();
        HttpUrl url = server.url("/fine.json");

        Request<Integer> request = new TestRequest(url);
        Result<Integer> result = request.execute();
        assertTrue(result.hasException());
        assertFalse(result.hasData());
        result.getOrThrow();
    }

    @Test(expected = RequestException.class)
    public void testWrongFormatString() throws IOException, RequestException {
        server.enqueue(new MockResponse().setBody("\"TEST\""));
        server.start();
        HttpUrl url = server.url("/fine.json");

        Request<Integer> request = new TestRequest(url);
        Result<Integer> result = request.execute();
        assertTrue(result.hasException());
        assertFalse(result.hasData());
        result.getOrThrow();
    }

    @Test(expected = RequestException.class)
    public void testWrongFormatText() throws IOException, RequestException {
        server.enqueue(new MockResponse().setBody("TEST"));
        server.start();
        HttpUrl url = server.url("/fine.json");

        Request<Integer> request = new TestRequest(url);
        Result<Integer> result = request.execute();
        assertTrue(result.hasException());
        assertFalse(result.hasData());
        result.getOrThrow();
    }

    @Test(expected = RequestException.class)
    public void testWrongFormatObject() throws IOException, RequestException {
        server.enqueue(new MockResponse().setBody("{\n" +
                "\t   \"rank\": \"4\",\n" +
                "\t   \"suit\": \"CLUBS\"\n" +
                "\t }"));
        server.start();
        HttpUrl url = server.url("/fine.json");

        Request<Integer> request = new TestRequest(url);
        Result<Integer> result = request.execute();
        assertTrue(result.hasException());
        assertFalse(result.hasData());
        result.getOrThrow();
    }

    @Test(expected = RequestException.class)
    public void testOfflineNoCache() throws IOException, RequestException {
        server.start();
        HttpUrl url = server.url("/fine.json");
        server.shutdown();

        Request<Integer> request = new TestRequest(url);
        Result<Integer> result = request.execute();
        assertTrue(result.hasException());
        assertFalse(result.hasData());
        result.getOrThrow();
    }

    @Test
    public void testOfflineWithFreshCache() throws IOException {
        server.enqueue(integerJsonResponse());
        server.start();
        HttpUrl url = server.url("/fine.json");

        // Put the request in the cache.
        Request<Integer> request = new TestRequest(url);
        Result<Integer> result = request.execute();
        assertTrue(result.hasData());
        assertEquals(1, (int) result.getData());

        server.shutdown();

        // Make another request.
        Result<Integer> result2 = request.execute();
        assertTrue(result2.hasData());
        assertEquals(1, (int) result2.getData());

        Cache cache = InstanceProvider.getClient(RuntimeEnvironment.application).cache();
        assertEquals(1, cache.networkCount());
        assertEquals(1, cache.hitCount());
        assertEquals(2, cache.requestCount());
    }

    @Test(expected = IOFailureException.class)
    public void testOfflineNotCacheable() throws IOException, RequestException {
        server.enqueue(integerJsonResponse());
        server.start();
        HttpUrl url = server.url("/fine.json");

        // Before we do a request, manually create the client and set the timeout low, to speed up the test.
        InstanceProvider.getClient(RuntimeEnvironment.application);

        // Put the request in the cache.
        Request<Integer> request = new NoCacheRequest(url);
        Result<Integer> result = request.execute();
        assertTrue(result.hasData());
        assertEquals(1, (int) result.getData());

        Cache cache = InstanceProvider.getClient(RuntimeEnvironment.application).cache();
        assertEquals(1, cache.networkCount());
        assertEquals(0, cache.hitCount());
        assertEquals(1, cache.requestCount());

        server.shutdown();

        // Make another request. Since we don't allow caching, this should not attempt to use the cache.
        Result<Integer> result2 = request.execute();
        assertFalse(result2.hasData());
        assertTrue(result2.hasException());

        result2.getOrThrow();
    }

    @Test
    public void testOfflineCacheable() throws IOException {
        server.enqueue(integerJsonResponse());
        server.start();
        HttpUrl url = server.url("/fine.json");

        // Before we do a request, manually create the client and set the timeout low, to speed up the test.
        InstanceProvider.getClient(RuntimeEnvironment.application);

        // Put the request in the cache.
        Request<Integer> request = new TestRequest(url);
        Result<Integer> result = request.execute();
        assertTrue(result.hasData());
        assertEquals(1, (int) result.getData());

        Cache cache = InstanceProvider.getClient(RuntimeEnvironment.application).cache();
        assertEquals(1, cache.networkCount());
        assertEquals(0, cache.hitCount());
        assertEquals(1, cache.requestCount());

        server.shutdown();

        // Make another request.
        Bundle args = new Bundle();
        args.putBoolean(BaseLiveData.REFRESH_COLD, true);
        Result<Integer> result2 = request.execute(args);
        assertTrue(result2.hasData());
        assertEquals(1, (int) result2.getData());

        assertEquals(3, cache.networkCount());
        assertEquals(1, cache.hitCount());
        assertEquals(4, cache.requestCount());
    }

    @Test(expected = RequestException.class)
    public void testResponseError() throws IOException, RequestException {
        server.enqueue(new MockResponse().setResponseCode(500));
        server.start();
        HttpUrl url = server.url("/fine.json");

        Request<Integer> request = new TestRequest(url);
        Result<Integer> result = request.execute();
        assertTrue(result.hasException());
        assertFalse(result.hasData());
        result.getOrThrow();
    }

    private static MockResponse integerJsonResponse() {
        return new MockResponse()
                .addHeader("Cache-Control", "max-age=" + Integer.MAX_VALUE)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody("1");
    }

    private static class TestRequest extends JsonOkHttpRequest<Integer> {

        private final HttpUrl url;
        private final Duration cacheDuration;

        TestRequest(HttpUrl url) {
            super(RuntimeEnvironment.application, Integer.class);
            this.url = url;
            this.cacheDuration = Duration.ofHours(1);
        }

        TestRequest(HttpUrl url, Duration duration) {
            super(RuntimeEnvironment.application, Integer.class);
            this.url = url;
            this.cacheDuration = duration;
        }

        @NonNull
        @Override
        protected String getAPIUrl() {
            return url.toString();
        }

        @Override
        protected Duration getCacheDuration() {
            return cacheDuration;
        }
    }

    private static class NoCacheRequest extends TestRequest {

        NoCacheRequest(HttpUrl url) {
            super(url);
        }

        @Override
        protected CacheControl constructCacheControl(@NonNull Bundle arguments) {
            return CacheControl.FORCE_NETWORK;
        }
    }
}