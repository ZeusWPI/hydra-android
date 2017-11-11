package be.ugent.zeus.hydra.data.network.requests;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.domain.models.info.InfoItem;
import be.ugent.zeus.hydra.data.network.Endpoints;
import be.ugent.zeus.hydra.data.network.JsonSpringRequest;
import be.ugent.zeus.hydra.repository.Cache;
import be.ugent.zeus.hydra.repository.requests.CacheableRequest;

/**
 * Request to get the information from the Zeus API.
 *
 * @author Juta
 */
public class InfoRequest extends JsonSpringRequest<InfoItem[]> implements CacheableRequest<InfoItem[]> {

    private static final String FILE_NAME = "info-content.json";

    public InfoRequest() {
        super(InfoItem[].class);
    }

    @NonNull
    @Override
    public String getCacheKey() {
        return FILE_NAME;
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.ZEUS_API_URL_2  + "info/" + FILE_NAME;
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_WEEK * 4;
    }
}