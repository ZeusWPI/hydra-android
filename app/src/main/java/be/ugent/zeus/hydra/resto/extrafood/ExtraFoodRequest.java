package be.ugent.zeus.hydra.resto.extrafood;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import org.threeten.bp.Duration;
import org.threeten.bp.temporal.ChronoUnit;

/**
 * Request to get the extra food.
 *
 * @author Niko Strijbol
 */
class ExtraFoodRequest extends JsonOkHttpRequest<ExtraFood> {

    ExtraFoodRequest(Context context) {
        super(context, ExtraFood.class);
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.ZEUS_V2 + "resto/extrafood.json";
    }

    @Override
    public Duration getCacheDuration() {
        return ChronoUnit.WEEKS.getDuration();
    }
}