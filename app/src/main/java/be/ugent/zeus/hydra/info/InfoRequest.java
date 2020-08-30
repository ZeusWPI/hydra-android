package be.ugent.zeus.hydra.info;

import android.content.Context;
import androidx.annotation.NonNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonArrayRequest;

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

    /**
     * Get the base API path for information. This is locale aware.
     */
    static String getBaseApiUrl(Context context) {
        String infoEndpoint = context.getString(R.string.value_info_endpoint);
        return Endpoints.ZEUS_V2 + "info/" + infoEndpoint + "/";
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return getBaseApiUrl(context) + "info-content.json";
    }

    @Override
    public Duration getCacheDuration() {
        return ChronoUnit.WEEKS.getDuration().multipliedBy(4);
    }
}