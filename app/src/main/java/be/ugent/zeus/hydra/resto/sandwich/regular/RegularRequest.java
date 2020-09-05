package be.ugent.zeus.hydra.resto.sandwich.regular;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonArrayRequest;
import be.ugent.zeus.hydra.common.request.Result;

/**
 * CacheRequest the list of sandwiches.
 *
 * @author feliciaan
 */
class RegularRequest extends JsonArrayRequest<RegularSandwich> {

    RegularRequest(Context context) {
        super(context, RegularSandwich.class);
    }

    @NonNull
    @Override
    public Result<List<RegularSandwich>> execute(@NonNull Bundle args) {
        return super.execute(args).map(sandwiches -> {
            Collections.sort(sandwiches, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
            return sandwiches;
        });
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.ZEUS_V2 + "resto/sandwiches/static.json";
    }

    @Override
    public Duration getCacheDuration() {
        return ChronoUnit.WEEKS.getDuration();
    }
}
