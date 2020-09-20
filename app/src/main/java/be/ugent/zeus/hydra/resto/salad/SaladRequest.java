package be.ugent.zeus.hydra.resto.salad;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonArrayRequest;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.resto.sandwich.regular.RegularSandwich;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

/**
 * Responsible for getting the list of salad bowls.
 *
 * @author NIko Strijbol
 */
class SaladRequest extends JsonArrayRequest<SaladBowl> {

    SaladRequest(Context context) {
        super(context, SaladBowl.class);
    }

    @NonNull
    @Override
    public Result<List<SaladBowl>> execute(@NonNull Bundle args) {
        return super.execute(args).map(sandwiches -> {
            Collections.sort(sandwiches, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
            return sandwiches;
        });
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.ZEUS_V2 + "resto/salads.json";
    }

    @Override
    public Duration getCacheDuration() {
        return ChronoUnit.WEEKS.getDuration();
    }
}
