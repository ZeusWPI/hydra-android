package be.ugent.zeus.hydra.requests;

import com.octo.android.robospice.persistence.DurationInMillis;

import be.ugent.zeus.hydra.models.specialevent.SpecialEventWrapper;

/**
 * Created by feliciaan on 06/04/16.
 */
public class SpecialEventRequest extends AbstractRequest<SpecialEventWrapper>{

    public SpecialEventRequest() {
        super(SpecialEventWrapper.class);
    }

    @Override
    public String getCacheKey() {
        return "specialEvents.json";
    }

    @Override
    protected String getAPIUrl() {
        return ZEUS_API_URL + "2.0/association/special_events.json";
    }

    @Override
    public long getCacheDuration() {
        return DurationInMillis.ONE_HOUR * 2;
    }
}
