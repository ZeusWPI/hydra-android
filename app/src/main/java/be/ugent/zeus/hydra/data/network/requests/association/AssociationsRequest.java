package be.ugent.zeus.hydra.data.network.requests.association;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.models.association.Association;
import be.ugent.zeus.hydra.data.network.Endpoints;
import be.ugent.zeus.hydra.data.network.JsonSpringRequest;
import be.ugent.zeus.hydra.data.network.caching.Cache;
import be.ugent.zeus.hydra.data.network.caching.CacheableRequest;

/**
 * Request to get all associations registered with the DSA.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class AssociationsRequest extends JsonSpringRequest<Association[]> implements CacheableRequest<Association[]> {

    private static final String FILENAME = "associations.json";

    public AssociationsRequest() {
        super(Association[].class);
    }

    @NonNull
    @Override
    public String getCacheKey() {
        return FILENAME;
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.DSA_API_URL + FILENAME;
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_WEEK * 4;
    }
}