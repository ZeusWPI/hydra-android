package be.ugent.zeus.hydra.requests.sko;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.cache.Cache;
import be.ugent.zeus.hydra.models.sko.Artists;
import be.ugent.zeus.hydra.requests.common.CacheableRequest;

/**
 * Request SKO lineup data.
 *
 * @author Niko Strijbol
 */
public class LineupRequest extends CacheableRequest<Artists> {

    private static final String URL = "http://studentkickoff.be/";

    public LineupRequest() {
        super(Artists.class);
    }

    @NonNull
    @Override
    public String getCacheKey() {
        return "artists.json";
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