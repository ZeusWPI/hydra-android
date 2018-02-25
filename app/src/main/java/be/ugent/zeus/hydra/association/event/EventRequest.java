package be.ugent.zeus.hydra.association.event;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.common.caching.Cache;
import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonSpringRequest;
import be.ugent.zeus.hydra.common.request.CacheableRequest;

/**
 * Get the activities for all associations.
 *
 * @author feliciaan
 * @author Niko Strijbol
 */
public class EventRequest extends JsonSpringRequest<Event[]> implements CacheableRequest<Event[]> {

    private static final String FILENAME = "all_activities.json";

    public EventRequest() {
        super(Event[].class);
    }

    @NonNull
    public String getCacheKey() {
        return FILENAME;
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.DSA_API_URL_3 + FILENAME;
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_HOUR;
    }
}