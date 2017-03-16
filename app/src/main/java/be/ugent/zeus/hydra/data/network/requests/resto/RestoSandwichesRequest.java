package be.ugent.zeus.hydra.data.network.requests.resto;

import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.data.network.caching.Cache;
import be.ugent.zeus.hydra.data.models.resto.Sandwiches;
import be.ugent.zeus.hydra.data.network.exceptions.RequestFailureException;

import java.util.Collections;

/**
 * CacheRequest the list of sandwiches.
 *
 * @author feliciaan
 */
public class RestoSandwichesRequest extends be.ugent.zeus.hydra.data.network.JsonSpringRequest<Sandwiches> implements be.ugent.zeus.hydra.data.network.caching.CacheableRequest<Sandwiches> {

    public RestoSandwichesRequest() {
        super(Sandwiches.class);
    }

    @NonNull
    @Override
    public Sandwiches performRequest() throws RequestFailureException {
        Sandwiches data = super.performRequest();
        Collections.sort(data, (lhs, rhs) -> lhs.name.compareToIgnoreCase(rhs.name));
        return data;
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
        return Cache.ONE_WEEK * 3;
    }
}