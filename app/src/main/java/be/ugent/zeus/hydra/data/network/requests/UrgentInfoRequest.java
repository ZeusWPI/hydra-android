package be.ugent.zeus.hydra.data.network.requests;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.models.UrgentProgramme;
import be.ugent.zeus.hydra.data.network.JsonSpringRequest;

/**
 * @author Niko Strijbol
 */
public class UrgentInfoRequest extends JsonSpringRequest<UrgentProgramme> {

    public UrgentInfoRequest() {
        super(UrgentProgramme.class);
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return "http://hydra.api.almiro.be/current";
    }
}
