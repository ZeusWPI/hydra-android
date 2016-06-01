package be.ugent.zeus.hydra.requests;

import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.loader.cache.Cache;
import be.ugent.zeus.hydra.models.resto.RestoMeta;

/**
 * Created by feliciaan on 04/02/16.
 */
public class RestoMetaRequest extends AbstractRequest<RestoMeta> {
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
