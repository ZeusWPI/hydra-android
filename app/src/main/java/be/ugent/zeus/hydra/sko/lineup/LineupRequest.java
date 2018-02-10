package be.ugent.zeus.hydra.sko.lineup;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonSpringRequest;
import be.ugent.zeus.hydra.common.caching.Cache;
import be.ugent.zeus.hydra.common.request.CacheableRequest;

/**
 * Request SKO lineup data.
 *
 * @author Niko Strijbol
 */
class LineupRequest extends JsonSpringRequest<Artist[]> implements CacheableRequest<Artist[]> {

    private static final String FILE_NAME = "artists.json";

    LineupRequest() {
        super(Artist[].class);
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