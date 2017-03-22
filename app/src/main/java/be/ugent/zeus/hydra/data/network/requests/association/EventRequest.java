package be.ugent.zeus.hydra.data.network.requests.association;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.models.association.Event;
import be.ugent.zeus.hydra.data.network.Endpoints;
import be.ugent.zeus.hydra.data.network.JsonSpringRequest;
import be.ugent.zeus.hydra.data.network.caching.Cache;
import be.ugent.zeus.hydra.data.network.caching.CacheableRequest;

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