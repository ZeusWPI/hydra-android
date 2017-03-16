package be.ugent.zeus.hydra.data.network.requests;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.models.info.InfoList;
import be.ugent.zeus.hydra.data.network.Endpoints;
import be.ugent.zeus.hydra.data.network.JsonSpringRequest;
import be.ugent.zeus.hydra.data.network.caching.Cache;
import be.ugent.zeus.hydra.data.network.caching.CacheableRequest;

/**
 * Request to get the information from the Zeus API.
 *
 * @author Juta
 */
public class InfoRequest extends JsonSpringRequest<InfoList> implements CacheableRequest<InfoList> {

    public InfoRequest() {
        super(InfoList.class);
    }

    @NonNull
    @Override
    public String getCacheKey() {
        return "info-content.json";
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.ZEUS_API_URL_2  + "info/info-content.json";
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_WEEK;
    }
}