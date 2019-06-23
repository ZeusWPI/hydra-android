package be.ugent.zeus.hydra.common.network;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import java.io.IOException;
import java.time.Duration;

import be.ugent.zeus.hydra.common.arch.data.BaseLiveData;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.RequestException;
import be.ugent.zeus.hydra.common.request.Result;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Test the default request. Includes tests for caching and offline handling. Therefor, when testing implementations of
 * this class, it is often enough to test the "everything OK" case, as other cases are covered by this test.
 *
 * @author Niko Strijbol
 */
@RunWith(AndroidJUnit4.class)
public class JsonOkHttpRequestTest {

    private MockWebServer server;
    private Context context;

    @Before
    public void setUp() {
        // Reset the singletons to have strictly separated tests.
        InstanceProvider.reset();
        server = new MockWebServer();
        context = ApplicationProvider.getApplicationContext();
        // Disable retry when offline.
        OkHttpClient.Builder builder = InstanceProvider.getBuilder(context.getCacheDir());
        builder.retryOnConnectionFailure(false);
        // Reduce the amount of time we wait for errors to speed up tests.
        builder.readTimeout(Duration.ofMillis(250));
        InstanceProvider.setClient(builder.build());
    }

    @After
    public void breakDown() throws IOException {
        server.shutdown();
    }

    @Test
    public void shouldUseNetwork_WhenOnlineAndNoCacheAvailable() throws IOException {
        server.enqueue(integerJsonResponse());
        server.start();

        HttpUrl url = server.url("/fine.json");

        Request<Integer> request = new TestRequest(url);
        Result<Integer> result = request.execute();

        assertTrue(result.hasData());
        assertEquals(1, (int) result.getData());
    }

    @Test
    public void shouldUseCache_WhenOnlineAndCacheAvailable() throws IOException {
        server.enqueue(integerJsonResponse());
        server.enqueue(integerJsonResponse());
        server.start();
        HttpUrl url = server.url("/fine.json");

        // The first request goes to the network and is cached.
        Request<Integer> request = new TestRequest(url);
        Result<Integer> result = request.execute();
        assertTrue(result.hasData());
        assertEquals(1, (int) result.getData());

        // The second request will not hit the network; the cache is used.
        Result<Integer> result2 = request.execute();
        assertTrue(result2.hasData());
        assertEquals(1, (int) result2.getData());

        Cache cache = InstanceProvider.getClient(context).cache();
        assertNotNull(cache);
        assertEquals(1, cache.networkCount());
        assertEquals(1, cache.hitCount());
        assertEquals(2, cache.requestCount());
    }

    @Test
    public void shouldUseNetwork_WhenOnlineAndCacheAvailableButRequestingFreshData() throws IOException {
        server.enqueue(integerJsonResponse(1));
        server.enqueue(integerJsonResponse(2));
        server.start();
        HttpUrl url = server.url("/fine.json");

        // The first request goes to the network and is cached.
        Request<Integer> request = new TestRequest(url);
        Result<Integer> result = request.execute();
        assertTrue(result.hasData());
        assertEquals(1, (int) result.getData());

        // The second request also hits the network; a cache is available, but we request fresh data.
        // This is used when the user refreshes a view manually.
        Bundle args = new Bundle();
        args.putBoolean(BaseLiveData.REFRESH_COLD, true);
        Result<Integer> result2 = request.execute(args);
        assertTrue(result2.hasData());
        assertEquals(2, (int) result2.getData());

        Cache cache = InstanceProvider.getClient(context).cache();
        assertNotNull(cache);
        assertEquals(2, cache.networkCount());
        assertEquals(0, cache.hitCount());
        assertEquals(2, cache.requestCount());
    }

    @Test
    public void shouldUseNetwork_WhenRequestIndicatedCacheDurationExpires() throws IOException {
        server.enqueue(integerJsonResponse(1));
        server.enqueue(integerJsonResponse(2));
        server.start();
        HttpUrl url = server.url("/fine.json");

        // The first request goes to the network and is cached, but the valid duration is set to zero.
        Request<Integer> request = new TestRequest(url, Duration.ZERO);
        Result<Integer> result = request.execute();
        assertTrue(result.hasData());
        assertEquals(1, (int) result.getData());

        // The second requests hits the network, since the cache is stale.
        Result<Integer> result2 = request.execute();
        assertTrue(result2.hasData());
        assertEquals(2, (int) result2.getData());

        Cache cache = InstanceProvider.getClient(context).cache();
        assertNotNull(cache);
        assertEquals(2, cache.networkCount());
        assertEquals(0, cache.hitCount());
        assertEquals(2, cache.requestCount());
    }

    @Test(expected = RequestException.class)
    public void shouldThrow_WhenResponseIsEmpty() throws IOException, RequestException {
        server.enqueue(new MockResponse());
        server.start();
        HttpUrl url = server.url("/fine.json");

        Request<Integer> request = new TestRequest(url);
        Result<Integer> result = request.execute();
        assertTrue(result.hasException());
        assertFalse(result.hasData());
        // Throw the exception to complete the test.
        result.getOrThrow();
    }

    @Test(expected = RequestException.class)
    public void shouldThrow_WhenResponseIsStringYetInvalidJson() throws IOException, RequestException {
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
    public void shouldTrow_WhenResponseIsNotEmptyYetWrongFormat() throws IOException, RequestException {
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
    public void shouldThrow_WhenResponseIsJsonYetExpectedOtherJson() throws IOException, RequestException {
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
    public void shouldThrow_whenOfflineAndNoCacheAvailable() throws RequestException {
        Request<Integer> request = new TestRequest(HttpUrl.get("http://hydra.invalid/fine.json"));
        Result<Integer> result = request.execute();
        assertTrue(result.hasException());
        assertFalse(result.hasData());
        result.getOrThrow();
    }

    @Test
    public void shouldUseCache_WhenOfflineAndValidCacheAvailable() throws IOException {
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

        Cache cache = InstanceProvider.getClient(context).cache();
        assertNotNull(cache);
        assertEquals(1, cache.networkCount());
        assertEquals(1, cache.hitCount());
        assertEquals(2, cache.requestCount());
    }

    @Test
    public void shouldUseCache_WhenOfflineAndCacheAvailableButRequestingFreshData() throws IOException {
        server.enqueue(integerJsonResponse());
        server.start();
        HttpUrl url = server.url("/fine.json");

        // Put the request in the cache.
        Request<Integer> request = new TestRequest(url);
        Result<Integer> result = request.execute();
        assertTrue(result.hasData());
        assertEquals(1, (int) result.getData());

        Cache cache = InstanceProvider.getClient(context).cache();
        assertNotNull(cache);
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

        assertEquals(2, cache.networkCount());
        assertEquals(1, cache.hitCount());
        assertEquals(3, cache.requestCount());
    }

    @Test(expected = IOFailureException.class)
    public void shouldThrow_WhenOfflineAndCacheAvailableButForbiddenToUseCache() throws IOException, RequestException {
        server.enqueue(integerJsonResponse());
        server.start();
        HttpUrl url = server.url("/fine.json");

        // Put the request in the cache.
        Request<Integer> request = new NoCacheRequest(url);
        Result<Integer> result = request.execute();
        assertTrue(result.hasData());
        assertEquals(1, (int) result.getData());

        Cache cache = InstanceProvider.getClient(context).cache();
        assertNotNull(cache);
        assertEquals(1, cache.networkCount());
        assertEquals(0, cache.hitCount());
        assertEquals(1, cache.requestCount());

        server.shutdown();

        // Make another request. Since we don't allow caching, this should not attempt to use the cache.
        Result<Integer> result2 = request.execute();
        assertFalse(result2.hasData());
        assertTrue(result2.hasException());
        assertEquals(0, cache.hitCount());

        result2.getOrThrow();
    }

    @Test
    public void shouldUseCache_WhenOfflineAndStaleCacheAvailable() throws IOException {
        server.enqueue(integerJsonResponse());
        server.start();
        HttpUrl url = server.url("/fine.json");

        // Put the request in the cache.
        Request<Integer> request = new TestRequest(url, Duration.ZERO);
        Result<Integer> result = request.execute();
        assertTrue(result.hasData());
        assertEquals(1, (int) result.getData());

        // Assert that the request was successful.
        Cache cache = InstanceProvider.getClient(context).cache();
        assertNotNull(cache);
        assertEquals(1, cache.networkCount());
        assertEquals(0, cache.hitCount());
        assertEquals(1, cache.requestCount());

        server.shutdown();

        // Make another request.
        Result<Integer> result2 = request.execute();
        assertTrue(result2.hasData());
        assertEquals(1, (int) result2.getData());

        assertEquals(2, cache.networkCount());
        assertEquals(1, cache.hitCount());
        assertEquals(3, cache.requestCount());
    }

    public void shouldUseCache_WhenOfflineAndStaleCacheAvailableButRequestingFreshData() throws IOException {
        server.enqueue(integerJsonResponse());
        server.start();
        HttpUrl url = server.url("/fine.json");

        // Put the request in the cache.
        Request<Integer> request = new TestRequest(url, Duration.ZERO);
        Result<Integer> result = request.execute();
        assertTrue(result.hasData());
        assertEquals(1, (int) result.getData());

        Cache cache = InstanceProvider.getClient(context).cache();
        assertNotNull(cache);
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
    public void shouldThrow_WhenHttpErrorOccurs() throws IOException, RequestException {
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
        return integerJsonResponse(1);
    }

    private static MockResponse integerJsonResponse(int i) {
        return new MockResponse()
                .addHeader("Cache-Control", "max-age=" + Integer.MAX_VALUE)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody(String.valueOf(i));
    }

    private static class TestRequest extends JsonOkHttpRequest<Integer> {

        private final HttpUrl url;
        private final Duration cacheDuration;

        TestRequest(HttpUrl url) {
            super(ApplicationProvider.getApplicationContext(), Integer.class);
            this.url = url;
            this.cacheDuration = Duration.ofHours(1);
        }

        TestRequest(HttpUrl url, Duration duration) {
            super(ApplicationProvider.getApplicationContext(), Integer.class);
            this.url = url;
            this.cacheDuration = duration;
        }

        @NonNull
        @Override
        protected String getAPIUrl() {
            return url.toString();
        }

        @Override
        protected org.threeten.bp.Duration getCacheDuration() {
            return org.threeten.bp.Duration.ofSeconds(cacheDuration.getSeconds());
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