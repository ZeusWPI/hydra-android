package be.ugent.zeus.hydra.data.network.caching;

import android.os.Bundle;
import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.data.network.exceptions.PartialDataException;
import be.ugent.zeus.hydra.data.network.exceptions.RequestException;

import java.io.Serializable;

/**
 * @author Niko Strijbol
 */
public class ErrorRequest implements CacheableRequest<ErrorRequest.Voider> {

    class Voider implements Serializable {}

    @NonNull
    @Override
    public Voider performRequest(Bundle args) throws RequestException, PartialDataException {
        throw new RequestException("Test: intentional exception.");
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