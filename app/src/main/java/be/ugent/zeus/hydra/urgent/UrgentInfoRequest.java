package be.ugent.zeus.hydra.urgent;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonSpringRequest;

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