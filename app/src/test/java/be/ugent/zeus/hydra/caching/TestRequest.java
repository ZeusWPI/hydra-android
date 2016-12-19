package be.ugent.zeus.hydra.caching;

import android.support.annotation.NonNull;

/**
 * @author Niko Strijbol
 */
public class TestRequest implements CacheableRequest<TestObject> {

    private boolean read;

    private final long cache;
    private final String key;

    public TestRequest(long cache, String key) {
        this.cache = cache;
        this.key = key;
    }

    @NonNull
    @Override
    public TestObject performRequest() {
        read = true;
        return new TestObject();
    }

    @NonNull
    @Override
    public String getCacheKey() {
        return key;
    }

    @Override
    public long getCacheDuration() {
        return cache;
    }

    public boolean isRead() {
        return read;
    }

    public void reset() {
        read = false;
    }
}
