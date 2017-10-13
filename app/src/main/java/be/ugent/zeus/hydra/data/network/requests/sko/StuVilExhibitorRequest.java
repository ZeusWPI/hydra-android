package be.ugent.zeus.hydra.data.network.requests.sko;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.models.sko.Exhibitor;
import be.ugent.zeus.hydra.data.network.Endpoints;
import be.ugent.zeus.hydra.data.network.JsonSpringRequest;
import be.ugent.zeus.hydra.repository.Cache;
import be.ugent.zeus.hydra.repository.requests.CacheableRequest;

/**
 * Get exhibitors in the Student Village.
 *
 * @author Niko Strijbol
 */
public class StuVilExhibitorRequest extends JsonSpringRequest<Exhibitor[]> implements CacheableRequest<Exhibitor[]> {

    private static final String FILE_NAME = "studentvillage.json";

    public StuVilExhibitorRequest() {
        super(Exhibitor[].class);
    }

    @NonNull
    @Override
    public String getCacheKey() {
        return FILE_NAME;
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_DAY;
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.SKO_URL + FILE_NAME;
    }
}