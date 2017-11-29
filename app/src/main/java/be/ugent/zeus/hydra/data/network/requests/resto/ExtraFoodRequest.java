package be.ugent.zeus.hydra.data.network.requests.resto;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.network.Endpoints;
import be.ugent.zeus.hydra.data.network.JsonSpringRequest;
import be.ugent.zeus.hydra.domain.models.resto.ExtraFood;
import be.ugent.zeus.hydra.repository.Cache;
import be.ugent.zeus.hydra.repository.requests.CacheableRequest;

/**
 * Request to get the extra food.
 *
 * @author Niko Strijbol
 */
public class ExtraFoodRequest extends JsonSpringRequest<ExtraFood> implements CacheableRequest<ExtraFood> {

    private static final String NAME = "extrafood.json";

    private static final String ENDPOINT = Endpoints.ZEUS_RESTO_URL + NAME;

    public ExtraFoodRequest() {
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