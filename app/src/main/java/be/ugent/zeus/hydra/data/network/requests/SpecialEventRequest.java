package be.ugent.zeus.hydra.data.network.requests;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.network.caching.Cache;
import be.ugent.zeus.hydra.data.models.specialevent.SpecialEventWrapper;

/**
 * Request to get special events (such as the 12urenloop).
 *
 * @author feliciaan
 */
public class SpecialEventRequest extends be.ugent.zeus.hydra.data.network.JsonSpringRequest<SpecialEventWrapper> implements be.ugent.zeus.hydra.data.network.caching.CacheableRequest<SpecialEventWrapper> {

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
        return ZEUS_API_URL + "2.0/association/special_events.json";
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_DAY;
    }
}