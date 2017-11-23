package be.ugent.zeus.hydra.data.network.requests.sko;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.domain.models.sko.Artist;
import be.ugent.zeus.hydra.data.network.Endpoints;
import be.ugent.zeus.hydra.data.network.JsonSpringRequest;
import be.ugent.zeus.hydra.repository.Cache;
import be.ugent.zeus.hydra.repository.requests.CacheableRequest;

/**
 * Request SKO lineup data.
 *
 * @author Niko Strijbol
 */
public class LineupRequest extends JsonSpringRequest<Artist[]> implements CacheableRequest<Artist[]> {

    private static final String FILE_NAME = "artists.json";

    public LineupRequest() {
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