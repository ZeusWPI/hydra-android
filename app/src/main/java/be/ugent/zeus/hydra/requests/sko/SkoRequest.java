package be.ugent.zeus.hydra.requests.sko;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.requests.common.CacheableRequest;

import java.io.Serializable;

/**
 * Request for SKO stuff.
 *
 * @author Niko Strijbol
 */
public abstract class SkoRequest<T extends Serializable> extends CacheableRequest<T> {

    protected static final String BASE_URL = "http://live.studentkickoff.be/";

    public SkoRequest(Class<T> clazz) {
        super(clazz);
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return BASE_URL + getCacheKey();
    }
}