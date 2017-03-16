package be.ugent.zeus.hydra.data.network.caching;

import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.data.network.exceptions.RequestFailureException;

import java.io.Serializable;

/**
 * @author Niko Strijbol
 */
public class ErrorRequest implements CacheableRequest<ErrorRequest.Voider> {

    class Voider implements Serializable {}

    @NonNull
    @Override
    public Voider performRequest() throws RequestFailureException {
        throw new RequestFailureException("Test: intentional exception.");
    }

    @NonNull
    @Override
    public String getCacheKey() {
        return "errorTest";
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_DAY;
    }
}