package be.ugent.zeus.hydra.resto.meta;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import org.threeten.bp.Duration;
import org.threeten.bp.temporal.ChronoUnit;

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
        return Endpoints.ZEUS_RESTO_URL + "meta.json";
    }

    @Override
    public Duration getCacheDuration() {
        return ChronoUnit.MONTHS.getDuration();
    }
}