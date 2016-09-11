package be.ugent.zeus.hydra.requests.sko;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.cache.Cache;
import be.ugent.zeus.hydra.models.sko.Stages;
import be.ugent.zeus.hydra.requests.common.CacheableRequest;

/**
 * Request SKO lineup data.
 *
 * @author Niko Strijbol
 */
public class LineupRequest extends CacheableRequest<Stages> {

    public LineupRequest() {
        super(Stages.class);
    }

    @NonNull
    @Override
    public String getCacheKey() {
        return "lineup.json";
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_DAY;
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return "http://live.studentkickoff.be/lineup.json";
    }
}
