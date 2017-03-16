package be.ugent.zeus.hydra.data.network.requests.sko;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.network.caching.Cache;
import be.ugent.zeus.hydra.data.models.sko.Artists;

/**
 * Request SKO lineup data.
 *
 * @author Niko Strijbol
 */
public class LineupRequest extends be.ugent.zeus.hydra.data.network.JsonSpringRequest<Artists> implements be.ugent.zeus.hydra.data.network.caching.CacheableRequest<Artists> {

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