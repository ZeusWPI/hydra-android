package be.ugent.zeus.hydra.requests;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.caching.Cache;
import be.ugent.zeus.hydra.models.info.InfoList;
import be.ugent.zeus.hydra.requests.common.CacheableRequest;

/**
 * Request to get the information from the Zeus API.
 *
 * @author Juta
 */
public class InfoRequest extends CacheableRequest<InfoList> {

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
        return ZEUS_API_URL + "/2.0/info/info-content.json";
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_DAY;
    }
}