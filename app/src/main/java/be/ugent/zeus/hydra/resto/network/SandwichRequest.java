package be.ugent.zeus.hydra.resto.network;

import android.os.Bundle;
import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.resto.Sandwich;
import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonSpringRequest;
import be.ugent.zeus.hydra.common.caching.Cache;
import be.ugent.zeus.hydra.common.request.CacheableRequest;
import be.ugent.zeus.hydra.common.request.Result;

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
    public Result<Sandwich[]> performRequest(Bundle args) {
        return super.performRequest(args).map(sandwiches -> {
            Arrays.sort(sandwiches, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
            return sandwiches;
        });
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
        return Cache.ONE_WEEK;
    }
}