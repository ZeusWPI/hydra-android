package be.ugent.zeus.hydra.resto.meta;

import android.content.Context;
import androidx.annotation.NonNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;

/**
 * Request for meta information about the resto.
 *
 * @author feliciaan
 */
public class MetaRequest extends JsonOkHttpRequest<RestoMeta> {

    public MetaRequest(Context context) {
        super(context, RestoMeta.class);
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.ZEUS_V2 + "resto/meta.json";
    }

    @Override
    public Duration getCacheDuration() {
        return ChronoUnit.MONTHS.getDuration();
    }
}