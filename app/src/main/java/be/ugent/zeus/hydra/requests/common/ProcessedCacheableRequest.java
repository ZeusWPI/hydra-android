package be.ugent.zeus.hydra.requests.common;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.cache.CacheRequest;

import java.io.Serializable;

/**
 * Cacheable class that needs additional processing on the cached data.
 *
 * @param <D> The cached data.
 * @param <R> The result of the request, the processed data.
 *
 * @author Niko Strijbol
 */
public abstract class ProcessedCacheableRequest<D extends Serializable, R> implements CacheRequest<D, R> {

    private final CacheRequest<D, ?> simpleRequest;

    protected ProcessedCacheableRequest(CacheRequest<D, ?> simpleRequest) {
        this.simpleRequest = simpleRequest;
    }

    @NonNull
    @Override
    public String getCacheKey() {
        return simpleRequest.getCacheKey();
    }

    @Override
    public long getCacheDuration() {
        return simpleRequest.getCacheDuration();
    }

    @NonNull
    @Override
    public D performRequest() throws RequestFailureException {
        return simpleRequest.performRequest();
    }
}