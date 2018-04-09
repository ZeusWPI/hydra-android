package be.ugent.zeus.hydra.specialevent;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import org.threeten.bp.Duration;

/**
 * Request to get special events (such as the 12urenloop).
 *
 * @author feliciaan
 */
public class SpecialEventRequest extends JsonOkHttpRequest<SpecialEventWrapper> {

    public SpecialEventRequest(Context context) {
        super(context, SpecialEventWrapper.class);
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.ZEUS_API_URL_2 + "association/special_events.json";
    }

    @Override
    public Duration getCacheDuration() {
        return Duration.ofDays(1);
    }
}