package be.ugent.zeus.hydra.common.caching;

import android.os.Bundle;
import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.repository.requests.RequestException;
import be.ugent.zeus.hydra.repository.requests.Result;
import be.ugent.zeus.hydra.repository.requests.CacheableRequest;

import java.io.Serializable;

/**
 * @author Niko Strijbol
 */
public class ErrorRequest implements CacheableRequest<ErrorRequest.Voider> {

    static class Voider implements Serializable {}

    @NonNull
    @Override
    public Result<Voider> performRequest(Bundle args) {
        return Result.Builder.fromException(new RequestException("Test: intentional exception."));
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