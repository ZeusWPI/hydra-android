package be.ugent.zeus.hydra.requests.resto;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.loader.cache.Cache;
import be.ugent.zeus.hydra.models.resto.RestoMeta;
import be.ugent.zeus.hydra.requests.common.CacheableRequest;

/**
 * Request for meta information about the resto.
 *
 * @author feliciaan
 */
public class RestoMetaRequest extends CacheableRequest<RestoMeta> {

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
        return ZEUS_API_URL + "2.0/resto/meta.json"; //TODO: check me!
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_HOUR * 6;
    }
}