package be.ugent.zeus.hydra.data.network.requests;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.models.UrgentInfo;
import be.ugent.zeus.hydra.data.network.JsonSpringRequest;

import static be.ugent.zeus.hydra.data.network.Endpoints.URGENT_API_URL;

/**
 * @author Niko Strijbol
 */
public class UrgentInfoRequest extends JsonSpringRequest<UrgentInfo> {

    public UrgentInfoRequest() {
        super(UrgentInfo.class);
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return URGENT_API_URL;
    }
}
