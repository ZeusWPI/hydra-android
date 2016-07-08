package be.ugent.zeus.hydra.requests;

import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.loader.cache.Cache;
import be.ugent.zeus.hydra.models.association.Associations;

/**
 * Created by feliciaan on 04/02/16.
 */
public class AssociationsRequest extends AbstractRequest<Associations> {

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
