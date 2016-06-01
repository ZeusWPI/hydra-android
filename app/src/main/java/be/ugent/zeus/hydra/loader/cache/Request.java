package be.ugent.zeus.hydra.loader.cache;

import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.loader.cache.exceptions.RequestFailureException;

/**
 * @author Niko Strijbol
 * @version 1/06/2016
 */
public interface Request<T> {

    @NonNull
    T performRequest() throws RequestFailureException;

    @NonNull
    String getCacheKey();

    long getCacheDuration();
}