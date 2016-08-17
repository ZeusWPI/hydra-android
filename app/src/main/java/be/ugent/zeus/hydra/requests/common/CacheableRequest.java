package be.ugent.zeus.hydra.requests.common;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.cache.CacheRequest;

import java.io.Serializable;

/**
 * Cacheable request. This is also the main abstract request for Zeus & DSA API requests. This is a simple cache request
 * that does not do any additional processing of the data.
 *
 * @param <T> The type of the result of the request.
 *
 * @author feliciaan
 */
public abstract class CacheableRequest<T extends Serializable> extends AbstractRequest<T> implements CacheRequest<T, T> {

    protected final String DSA_API_URL = "http://student.ugent.be/hydra/api/2.0/";
    protected final String ZEUS_API_URL = "https://zeus.UGent.be/hydra/api/";

    public CacheableRequest(Class<T> clazz) {
        super(clazz);
    }

    @NonNull
    @Override
    public T getData(@NonNull T data) {
        return data;
    }
}