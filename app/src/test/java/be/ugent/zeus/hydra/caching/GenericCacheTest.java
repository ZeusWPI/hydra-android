package be.ugent.zeus.hydra.caching;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;
import org.junit.Before;
import org.junit.Test;
import org.threeten.bp.Instant;
import org.threeten.bp.temporal.ChronoUnit;

import java.io.File;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Niko Strijbol
 */
public class GenericCacheTest {

    private GenericCache cache;
    private TestExecutor executor;

    private static final long MILLIS = Instant.now().toEpochMilli();
    private static final int BUILD = BuildConfig.VERSION_CODE;

    @Before
    public void setUp() throws Exception {
        executor = new TestExecutor();
        cache = new GenericCache(new File(""), executor);
    }

    @Test
    public void get() throws Exception {
        TestRequest request = new TestRequest(Cache.ONE_HOUR, TestObject.TEST_FILE_KEY);

        //Test cacheable request
        TestObject result = cache.get(request);
        assertFalse(request.isRead());
        assertEquals(result, request.performRequest());
        request.reset();

        executor.setUpdated(Instant.now().toEpochMilli());
        cache.get(request, Cache.ONE_SECOND);
        cache.get(request, Cache.ONE_MINUTE);
        cache.get(request, Cache.ONE_HOUR);
        cache.get(request, Cache.ONE_DAY);
        cache.get(request, Cache.ONE_WEEK);
        cache.get(request, Cache.ALWAYS);
        assertFalse(request.isRead());
        assertFalse(executor.isHasSaved());
        request.reset();
        executor.reset();

        cache.get(request, Cache.NEVER);
        assertTrue(request.isRead());
        assertTrue(executor.isHasSaved());
        executor.reset();
        request.reset();
    }

    @Test(expected = RequestFailureException.class)
    public void getException() throws RequestFailureException {
        ErrorRequest errorRequest = new ErrorRequest();

        executor.setUpdated(Instant.now().minus(31, ChronoUnit.DAYS).toEpochMilli());
        cache.get(errorRequest);
    }

    @Test
    public void getExceptionNull() throws RequestFailureException {
        ErrorRequest errorRequest = new ErrorRequest();

        executor.setUpdated(Instant.now().minus(31, ChronoUnit.DAYS).toEpochMilli());
        assertNull(cache.getOrNull(errorRequest));
    }

    @Test
    public void getNonExistingNull() throws RequestFailureException {
        TestRequest request = new TestRequest(Cache.ONE_SECOND, "TestKeyNonExisting");

        executor.setUpdated(Instant.now().minus(31, ChronoUnit.DAYS).toEpochMilli());
        cache.get(request);
        assertTrue(request.isRead());
    }

    @Test
    public void getExpired() throws Exception {
        TestRequest request = new TestRequest(Cache.ONE_HOUR, TestObject.TEST_FILE_KEY);

        executor.setUpdated(Instant.now().minus(31, ChronoUnit.DAYS).toEpochMilli());

        //Test cacheable request
        TestObject result = cache.get(request);
        assertTrue(request.isRead());
        assertEquals(result, request.performRequest());
        request.reset();

        cache.get(request, Cache.ONE_SECOND);
        assertTrue(request.isRead());
        assertTrue(executor.isHasSaved());
        executor.reset();
        request.reset();
        cache.get(request, Cache.ONE_MINUTE);
        assertTrue(request.isRead());
        assertTrue(executor.isHasSaved());
        executor.reset();
        request.reset();
        cache.get(request, Cache.ONE_HOUR);
        assertTrue(request.isRead());
        assertTrue(executor.isHasSaved());
        executor.reset();
        request.reset();
        cache.get(request, Cache.ONE_DAY);
        assertTrue(request.isRead());
        assertTrue(executor.isHasSaved());
        executor.reset();
        request.reset();
        cache.get(request, Cache.ONE_WEEK);
        assertTrue(request.isRead());
        assertTrue(executor.isHasSaved());
        executor.reset();
        request.reset();
        cache.get(request, Cache.NEVER);
        assertTrue(request.isRead());
        assertTrue(executor.isHasSaved());
        executor.reset();
        request.reset();

        cache.get(request, Cache.ALWAYS);
        assertFalse(request.isRead());
    }

    @Test
    public void shouldRefresh() throws Exception {
        assertTrue(cache.shouldRefresh(null, Cache.ALWAYS));

        CacheObject<TestObject> object = new CacheObject<>(new TestObject());
        assertFalse(cache.shouldRefresh(object, Cache.ONE_SECOND));
        assertFalse(cache.shouldRefresh(object, Cache.ONE_MINUTE));
        assertFalse(cache.shouldRefresh(object, Cache.ONE_HOUR));
        assertFalse(cache.shouldRefresh(object, Cache.ONE_WEEK));

        assertTrue(cache.shouldRefresh(object, Cache.NEVER));
        assertFalse(cache.shouldRefresh(object, Cache.ALWAYS));
    }

    @Test
    public void shouldRefreshOld() {
        long monthAgo = Instant.now().minus(31, ChronoUnit.DAYS).toEpochMilli();
        CacheObject<TestObject> object = new CacheObject<>(monthAgo, new TestObject(), BuildConfig.VERSION_CODE);

        assertTrue(cache.shouldRefresh(object, Cache.ONE_SECOND));
        assertTrue(cache.shouldRefresh(object, Cache.ONE_MINUTE));
        assertTrue(cache.shouldRefresh(object, Cache.ONE_HOUR));
        assertTrue(cache.shouldRefresh(object, Cache.ONE_WEEK));

        assertTrue(cache.shouldRefresh(object, Cache.NEVER));
        assertFalse(cache.shouldRefresh(object, Cache.ALWAYS));
    }

    @Test
    public void shouldRefreshCode() {
        CacheObject<TestObject> object = new CacheObject<>(Instant.now().toEpochMilli(), new TestObject(), 0);

        assertTrue(cache.shouldRefresh(object, Cache.ONE_SECOND));
        assertTrue(cache.shouldRefresh(object, Cache.ONE_MINUTE));
        assertTrue(cache.shouldRefresh(object, Cache.ONE_HOUR));
        assertTrue(cache.shouldRefresh(object, Cache.ONE_WEEK));

        assertTrue(cache.shouldRefresh(object, Cache.NEVER));
        assertFalse(cache.shouldRefresh(object, Cache.ALWAYS));
    }
}