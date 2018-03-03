package be.ugent.zeus.hydra.common.request;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.common.caching.Cache;
import java8.util.function.Function;

/**
 * A request that provides additional metadata about the requested data to facilitate caching of said data.
 * This is especially useful when getting data from a remote source, or when calculating the data is very resource
 * intensive.
 *
 * Note that this request is not responsible for the caching itself, this interface merely indicates that the data can
 * be cached. The request should thus behave like a normal {@link Request} and always return fresh data.
 *
 * It is up to the caller to decide which data to use and which not. Refer to the {@link Cache} implementation for
 * details.
 *
 * @param <D> Type of data that is cached.
 *
 * @author Niko Strijbol
 */
public interface CacheableRequest<D> extends Request<D> {

    /**
     * Provide a suggestion to the caller under which name this data can be saved.
     *
     * @return The key under which the result will be stored by the {@link Cache}.
     */
    @NonNull
    String getCacheKey();

    /**
     * Provide a suggestion for the maximal cache duration.
     *
     * @return The maximal duration the data should be cached.
     */
    long getCacheDuration();

    /**
     * Construct a new cacheable request where a given function has been applied to the output of this request.
     * The cache key and duration are still the same.
     *
     * @param function The function to apply.
     *
     * @param <R> The resulting type.
     *
     * @return The new request.
     */
    @Override
    default <R> CacheableRequest<R> map(Function<D, R> function) {
        CacheableRequest<D> r = this;
        return new CacheableRequest<R>() {
            @NonNull
            @Override
            public String getCacheKey() {
                return r.getCacheKey();
            }

            @Override
            public long getCacheDuration() {
                return r.getCacheDuration();
            }

            @NonNull
            @Override
            public Result<R> performRequest(@Nullable Bundle args) {
                return r.performRequest(args).map(function);
            }
        };
    }

}