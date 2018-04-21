package be.ugent.zeus.hydra.sko.studentvillage;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonArrayRequest;
import org.threeten.bp.Duration;

/**
 * Get exhibitors in the Student Village.
 *
 * @author Niko Strijbol
 */
class ExhibitorRequest extends JsonArrayRequest<Exhibitor> {

    ExhibitorRequest(Context context) {
        super(context, Exhibitor.class);
    }

    @Override
    public Duration getCacheDuration() {
        return Duration.ofDays(1);
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.SKO_URL + "studentvillage.json";
    }
}