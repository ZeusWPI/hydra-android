package be.ugent.zeus.hydra.resto.sandwich.ecological;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.List;

import java9.util.Comparators;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonArrayRequest;
import be.ugent.zeus.hydra.common.request.Result;
import org.threeten.bp.Duration;
import org.threeten.bp.temporal.ChronoUnit;

/**
 * Get the ecological sandwich of the week.
 *
 * @author feliciaan
 */
class EcologicalRequest extends JsonArrayRequest<EcologicalSandwich> {

    EcologicalRequest(Context context) {
        super(context, EcologicalSandwich.class);
    }

    @NonNull
    @Override
    public Result<List<EcologicalSandwich>> execute(@NonNull Bundle args) {
        return super.execute(args).map(sandwiches -> {
            Collections.sort(sandwiches, Comparators.comparing(EcologicalSandwich::getStart));
            return sandwiches;
        });
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.ZEUS_V2 + "resto/sandwiches/overview.json";
    }

    @Override
    public Duration getCacheDuration() {
        return ChronoUnit.WEEKS.getDuration();
    }
}
