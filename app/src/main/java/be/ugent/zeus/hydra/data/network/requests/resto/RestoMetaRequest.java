package be.ugent.zeus.hydra.data.network.requests.resto;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.network.caching.Cache;
import be.ugent.zeus.hydra.data.models.resto.RestoMeta;

/**
 * Request for meta information about the resto.
 *
 * @author feliciaan
 */
public class RestoMetaRequest extends be.ugent.zeus.hydra.data.network.JsonSpringRequest<RestoMeta> implements be.ugent.zeus.hydra.data.network.caching.CacheableRequest<RestoMeta> {

    public RestoMetaRequest() {
        super(RestoMeta.class);
    }

    @NonNull
    @Override
    public String getCacheKey() {
        return "restoMeta.json";
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return ZEUS_API_URL + "2.0/resto/meta.json";
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_WEEK * 8;
    }
}