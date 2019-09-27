package be.ugent.zeus.hydra.association.preference;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.List;

import java9.util.Comparators;

import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonArrayRequest;
import be.ugent.zeus.hydra.common.request.Result;
import org.threeten.bp.Duration;
import org.threeten.bp.temporal.ChronoUnit;

/**
 * Request to get all associations registered with the DSA.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
class AssociationsRequest extends JsonArrayRequest<Association> {

    private static final String FILENAME = "associations.json";

    AssociationsRequest(Context context) {
        super(context, Association.class);
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.DSA_V3 + FILENAME;
    }

    @Override
    public Duration getCacheDuration() {
        return ChronoUnit.WEEKS.getDuration().multipliedBy(4);
    }

    @NonNull
    @Override
    public Result<List<Association>> execute(@NonNull Bundle args) {
        return super.execute(args).map(associations -> {
            Collections.sort(associations, Comparators.comparing(Association::getName));
            return associations;
        });
    }
}