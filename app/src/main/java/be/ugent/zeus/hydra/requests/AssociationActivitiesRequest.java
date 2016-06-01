package be.ugent.zeus.hydra.requests;

import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.loader.cache.Cache;
import be.ugent.zeus.hydra.loader.cache.Request;
import be.ugent.zeus.hydra.models.association.AssociationActivities;

/**
 * Created by feliciaan on 27/01/16.
 */
public class AssociationActivitiesRequest extends AbstractRequest<AssociationActivities> implements Request<AssociationActivities> {

    public AssociationActivitiesRequest() {
        super(AssociationActivities.class);
    }

    @NonNull
    public String getCacheKey() {
        return "all_activities.json";
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return DSA_API_URL + "all_activities.json";
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_MINUTE * 15;
    }
}
