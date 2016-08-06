package be.ugent.zeus.hydra.requests;

import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.cache.Cache;
import be.ugent.zeus.hydra.models.association.Associations;
import be.ugent.zeus.hydra.requests.common.CacheableRequest;

/**
 * Request to get all associations registered with the DSA.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class AssociationsRequest extends CacheableRequest<Associations> {

    public AssociationsRequest() {
        super(Associations.class);
    }

    @NonNull
    @Override
    public String getCacheKey() {
        return "associations.json";
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return DSA_API_URL + "associations.json";
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_DAY;
    }
}