package be.ugent.zeus.hydra.requests;

import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.loader.cache.Cache;
import be.ugent.zeus.hydra.models.info.InfoList;

/**
 * Created by Juta on 03/03/2016.
 */
public class InfoRequest extends AbstractRequest<InfoList> {

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
        return Cache.ONE_HOUR * 6;
    }
}
