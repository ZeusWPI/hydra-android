package be.ugent.zeus.hydra.association;

import android.content.Context;
import androidx.annotation.NonNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import be.ugent.zeus.hydra.common.request.Request;

/**
 * Request to get all associations registered with the DSA.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class AssociationListRequest extends JsonOkHttpRequest<AssociationList> {

    private static final String FILENAME = "verenigingen";

    AssociationListRequest(Context context) {
        super(context, AssociationList.class);
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.DSA_V4 + FILENAME;
    }

    @Override
    public Duration getCacheDuration() {
        return ChronoUnit.WEEKS.getDuration().multipliedBy(4);
    }
    
    public static Request<List<Association>> asList(Context context) {
        return new AssociationListRequest(context).map(AssociationList::getAssociations);
    }
}