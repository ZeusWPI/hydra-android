package be.ugent.zeus.hydra.info;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonArrayRequest;
import org.threeten.bp.Duration;
import org.threeten.bp.temporal.ChronoUnit;

/**
 * Request to get the information from the Zeus API.
 *
 * @author Juta
 */
class InfoRequest extends JsonArrayRequest<InfoItem> {

    InfoRequest(Context context) {
        super(context, InfoItem.class);
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.ZEUS_API_URL_2  + "info/info-content.json";
    }

    @Override
    public Duration getCacheDuration() {
        return ChronoUnit.WEEKS.getDuration().multipliedBy(4);
    }
}