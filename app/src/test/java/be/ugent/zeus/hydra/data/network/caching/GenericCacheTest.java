package be.ugent.zeus.hydra.data.network.caching;

import android.content.Context;
import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.data.network.exceptions.PartialDataException;
import be.ugent.zeus.hydra.data.network.exceptions.RequestException;
import org.junit.Before;
import org.junit.Test;
import org.threeten.bp.Instant;
import org.threeten.bp.temporal.ChronoUnit;

import java.io.File;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Niko Strijbol
 */
public class GenericCacheTest {

    private GenericCache cache;
    private TestExecutor executor;

    @Before
    public void setUp() throws Exception {
        executor = new TestExecutor();
        cache = new GenericCache(new File(""), executor);
    }

    @Test
    public void constructor() {
        Context context = mock(Context.class);
        when(context.getCacheDir()).thenReturn(new File(""));
        GenericCache cache = new GenericCache(context);
        assertEquals(cache.getDirectory(), new File(""));
    }

    @Test
    public void get() throws Exception {
        TestRequest request = new TestRequest(Cache.ONE_HOUR, TestObject.TEST_FILE_KEY);

        //Test cacheable request
        TestObject result = cache.get(request, null);
        assertFalse(request.isRead());
        assertEquals(result, request.performRequest(null));
        request.reset();

        executor.setUpdated(Instant.now().toEpochMilli());
        cache.get(request, null, Cache.ONE_SECOND);
        cache.get(request, null, Cache.ONE_MINUTE);
        cache.get(request, null, Cache.ONE_HOUR);
        cache.get(request, null, Cache.ONE_DAY);
        cache.get(request, null, Cache.ONE_WEEK);
        cache.get(request, null, Cache.ALWAYS);
        assertFalse(request.isRead());
        assertFalse(executor.isHasSaved());
        request.reset();
        executor.reset();

        cache.get(request, null, Cache.NEVER);
        assertTrue(request.isRead());
        assertTrue(executor.isHasSaved());
        executor.reset();
        request.reset();
    }

    @Test(expected = RequestException.class)
    public void getException() throws RequestException, PartialDataException {
        ErrorRequest errorRequest = new ErrorRequest();

        executor.setUpdated(Instant.now().minus(31, ChronoUnit.DAYS).toEpochMilli());
        cache.get(errorRequest, null);
    }

    @Test
    public void getNonExistingNull() throws RequestException, PartialDataException {
        TestRequest request = new TestRequest(Cache.ONE_SECOND, "TestKeyNonExisting");

        executor.setUpdated(Instant.now().minus(31, ChronoUnit.DAYS).toEpochMilli());
        cache.get(request, null);
        assertTrue(request.isRead());
    }

    @Test
    public void getExpired() throws Exception {
        TestRequest request = new TestRequest(Cache.ONE_HOUR, TestObject.TEST_FILE_KEY);

        executor.setUpdated(Instant.now().minus(31, ChronoUnit.DAYS).toEpochMilli());

        //Test cacheable request
        TestObject result = cache.get(request, null);
        assertTrue(request.isRead());
        assertEquals(result, request.performRequest(null));
        request.reset();

        cache.get(request, null, Cache.ONE_SECOND);
        assertTrue(request.isRead());
        assertTrue(executor.isHasSaved());
        executor.reset();
        request.reset();
        cache.get(request, null, Cache.ONE_MINUTE);
        assertTrue(request.isRead());
        assertTrue(executor.isHasSaved());
        executor.reset();
        request.reset();
        cache.get(request, null, Cache.ONE_HOUR);
        assertTrue(request.isRead());
        assertTrue(executor.isHasSaved());
        executor.reset();
        request.reset();
        cache.get(request, null, Cache.ONE_DAY);
        assertTrue(request.isRead());
        assertTrue(executor.isHasSaved());
        executor.reset();
        request.reset();
        cache.get(request, null, Cache.ONE_WEEK);
        assertTrue(request.isRead());
        assertTrue(executor.isHasSaved());
        executor.reset();
        request.reset();
        cache.get(request, null, Cache.NEVER);
        assertTrue(request.isRead());
        assertTrue(executor.isHasSaved());
        executor.reset();
        request.reset();

        cache.get(request, null, Cache.ALWAYS);
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
    public void isExpired() throws Exception {
        executor.setUpdated(Instant.now().toEpochMilli());
        assertFalse(cache.isExpired(TestObject.TEST_FILE_KEY, Cache.ONE_HOUR));
        executor.setUpdated(Instant.now().minus(31, ChronoUnit.DAYS).toEpochMilli());
        assertTrue(cache.isExpired(TestObject.TEST_FILE_KEY, Cache.ONE_HOUR));
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