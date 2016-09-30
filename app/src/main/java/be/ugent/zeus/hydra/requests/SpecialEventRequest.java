package be.ugent.zeus.hydra.requests;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.caching.Cache;
import be.ugent.zeus.hydra.models.specialevent.SpecialEventWrapper;
import be.ugent.zeus.hydra.requests.common.CacheableRequest;

/**
 * Request to get special events (such as the 12urenloop).
 *
 * @author feliciaan
 */
public class SpecialEventRequest extends CacheableRequest<SpecialEventWrapper> {

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
        return Cache.ONE_HOUR * 2;
    }
}