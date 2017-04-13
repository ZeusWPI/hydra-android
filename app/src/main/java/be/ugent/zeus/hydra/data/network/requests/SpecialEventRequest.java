package be.ugent.zeus.hydra.data.network.requests;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.models.specialevent.SpecialEventWrapper;
import be.ugent.zeus.hydra.data.network.Endpoints;
import be.ugent.zeus.hydra.data.network.JsonSpringRequest;
import be.ugent.zeus.hydra.data.network.caching.Cache;
import be.ugent.zeus.hydra.data.network.caching.CacheableRequest;

/**
 * Request to get special events (such as the 12urenloop).
 *
 * @author feliciaan
 */
public class SpecialEventRequest extends JsonSpringRequest<SpecialEventWrapper> implements CacheableRequest<SpecialEventWrapper> {

    public SpecialEventRequest() {
        super(SpecialEventWrapper.class);
    }

    @NonNull
    @Override
    public String getCacheKey() {
        return "specialEvents.json";
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.ZEUS_API_URL_2 + "association/special_events.json";
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_DAY;
    }
}