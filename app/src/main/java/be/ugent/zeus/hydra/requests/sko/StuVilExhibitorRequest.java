package be.ugent.zeus.hydra.requests.sko;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.caching.Cache;
import be.ugent.zeus.hydra.models.sko.Exhibitors;
import be.ugent.zeus.hydra.requests.common.CacheableRequest;

/**
 * Get exhibitors in the Student Village.
 *
 * @author Niko Strijbol
 */
public class StuVilExhibitorRequest extends CacheableRequest<Exhibitors> {

    private static final String URL = "http://studentkickoff.be/";

    public StuVilExhibitorRequest() {
        super(Exhibitors.class);
    }

    @NonNull
    @Override
    public String getCacheKey() {
        return "studentvillage.json";
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_DAY;
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return URL + getCacheKey();
    }
}
