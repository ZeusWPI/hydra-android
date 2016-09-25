package be.ugent.zeus.hydra.requests;

import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.cache.Cache;
import be.ugent.zeus.hydra.models.association.Activities;
import be.ugent.zeus.hydra.requests.common.CacheableRequest;

/**
 * Get the activities for all associations.
 *
 * @author feliciaan
 * @author Niko Strijbol
 */
public class ActivitiesRequest extends CacheableRequest<Activities> {

    public ActivitiesRequest() {
        super(Activities.class);
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