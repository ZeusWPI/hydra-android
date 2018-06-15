package be.ugent.zeus.hydra.info;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.R;
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

    private final Context context;

    InfoRequest(Context context) {
        super(context, InfoItem.class);
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return getBaseApiUrl(context) + "info-content.json";
    }

    /**
     * Get the base API path for information. This is locale aware.
     */
    static String getBaseApiUrl(Context context) {
        String infoEndpoint = context.getString(R.string.value_info_endpoint);
        return Endpoints.ZEUS_V2 + "info/" + infoEndpoint + "/";
    }

    @Override
    public Duration getCacheDuration() {
        return ChronoUnit.WEEKS.getDuration().multipliedBy(4);
    }
}