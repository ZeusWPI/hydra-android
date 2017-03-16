package be.ugent.zeus.hydra.data.network;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.network.caching.CacheableRequest;
import be.ugent.zeus.hydra.data.network.exceptions.RequestFailureException;

import java.util.Arrays;
import java.util.List;

/**
 * A request that converts the array result of another request to a list request.
 *
 * @author Niko Strijbol
 */
public class CacheableFromArrayRequest<R> extends FromArrayRequest<R> implements CacheableRequest<List<R>> {

    private final CacheableRequest<R[]> wrapping;

    public CacheableFromArrayRequest(CacheableRequest<R[]> wrapping) {
        super(wrapping);
        this.wrapping = wrapping;
    }

    @NonNull
    @Override
    public List<R> performRequest() throws RequestFailureException {
        return Arrays.asList(wrapping.performRequest());
    }

    @NonNull
    @Override
    public String getCacheKey() {
        return wrapping.getCacheKey();
    }

    @Override
    public long getCacheDuration() {
        return wrapping.getCacheDuration();
    }
}