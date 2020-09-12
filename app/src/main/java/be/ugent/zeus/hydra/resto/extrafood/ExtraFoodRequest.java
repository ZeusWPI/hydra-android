package be.ugent.zeus.hydra.resto.extrafood;

import android.content.Context;
import androidx.annotation.NonNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;

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