package be.ugent.zeus.hydra.requests.association;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.caching.Cache;
import be.ugent.zeus.hydra.models.association.Events;
import be.ugent.zeus.hydra.requests.common.CacheableRequest;

/**
 * Get the activities for all associations.
 *
 * @author feliciaan
 * @author Niko Strijbol
 */
public class EventRequest extends CacheableRequest<Events> {

    public EventRequest() {
        super(Events.class);
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