package be.ugent.zeus.hydra.data.network.requests.resto;

import android.os.Bundle;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.models.resto.Sandwich;
import be.ugent.zeus.hydra.data.network.Endpoints;
import be.ugent.zeus.hydra.data.network.JsonSpringRequest;
import be.ugent.zeus.hydra.data.network.caching.Cache;
import be.ugent.zeus.hydra.data.network.caching.CacheableRequest;
import be.ugent.zeus.hydra.data.network.exceptions.RequestFailureException;

import java.util.Arrays;

/**
 * CacheRequest the list of sandwiches.
 *
 * @author feliciaan
 */
public class SandwichRequest extends JsonSpringRequest<Sandwich[]> implements CacheableRequest<Sandwich[]> {

    private static final String FILE_NAME = "sandwiches.json";

    public SandwichRequest() {
        super(Sandwich[].class);
    }

    @NonNull
    @Override
    public Sandwich[] performRequest(Bundle args) throws RequestFailureException {
        Sandwich[] data = super.performRequest(args);
        Arrays.sort(data, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
        return data;
    }

    @NonNull
    @Override
    public String getCacheKey() {
        return FILE_NAME;
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.ZEUS_RESTO_URL + FILE_NAME;
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_WEEK * 3;
    }
}