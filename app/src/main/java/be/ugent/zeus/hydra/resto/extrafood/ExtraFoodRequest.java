package be.ugent.zeus.hydra.resto.extrafood;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonSpringRequest;
import be.ugent.zeus.hydra.common.caching.Cache;
import be.ugent.zeus.hydra.common.request.CacheableRequest;

/**
 * Request to get the extra food.
 *
 * @author Niko Strijbol
 */
class ExtraFoodRequest extends JsonSpringRequest<ExtraFood> implements CacheableRequest<ExtraFood> {

    private static final String NAME = "extrafood.json";

    private static final String ENDPOINT = Endpoints.ZEUS_RESTO_URL + NAME;

    ExtraFoodRequest() {
        super(ExtraFood.class);
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return ENDPOINT;
    }

    @NonNull
    @Override
    public String getCacheKey() {
        return NAME;
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_WEEK;
    }
}