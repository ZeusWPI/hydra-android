package be.ugent.zeus.hydra.data.network.requests.urgent;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.models.UrgentInfo;
import be.ugent.zeus.hydra.data.network.Endpoints;
import be.ugent.zeus.hydra.data.network.JsonSpringRequest;

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
        return Endpoints.URGENT_STATUS_URL;
    }
}