package be.ugent.zeus.hydra.requests;

import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.loader.cache.Cache;
import be.ugent.zeus.hydra.models.resto.Sandwiches;

/**
 * CacheRequest the list of sandwiches.
 *
 * @author feliciaan
 */
public class RestoSandwichesRequest extends AbstractRequest<Sandwiches> {

    public RestoSandwichesRequest() {
        super(Sandwiches.class);
    }

    @NonNull
    @Override
    public String getCacheKey() {
        return "sandwiches.json";
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return ZEUS_API_URL + "2.0/resto/sandwiches.json";
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_HOUR * 12;
    }
}