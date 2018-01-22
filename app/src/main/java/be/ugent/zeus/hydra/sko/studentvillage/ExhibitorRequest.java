package be.ugent.zeus.hydra.sko.studentvillage;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonSpringRequest;
import be.ugent.zeus.hydra.common.caching.Cache;
import be.ugent.zeus.hydra.common.request.CacheableRequest;

/**
 * Get exhibitors in the Student Village.
 *
 * @author Niko Strijbol
 */
class ExhibitorRequest extends JsonSpringRequest<Exhibitor[]> implements CacheableRequest<Exhibitor[]> {

    private static final String FILE_NAME = "studentvillage.json";

    ExhibitorRequest() {
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