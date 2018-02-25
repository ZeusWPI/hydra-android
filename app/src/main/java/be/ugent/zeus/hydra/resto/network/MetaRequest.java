package be.ugent.zeus.hydra.resto.network;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.resto.RestoMeta;
import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonSpringRequest;
import be.ugent.zeus.hydra.common.caching.Cache;
import be.ugent.zeus.hydra.common.request.CacheableRequest;

/**
 * Request for meta information about the resto.
 *
 * @author feliciaan
 */
public class MetaRequest extends JsonSpringRequest<RestoMeta> implements CacheableRequest<RestoMeta> {

    private static final String NAME = "meta.json";

    public MetaRequest() {
        super(RestoMeta.class);
    }

    @NonNull
    @Override
    public String getCacheKey() {
        return "resto" + NAME;
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.ZEUS_RESTO_URL + NAME;
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_WEEK * 4;
    }
}