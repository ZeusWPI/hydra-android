package be.ugent.zeus.hydra.requests;

import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.loader.cache.Cache;
import be.ugent.zeus.hydra.models.schamper.Articles;

/**
 * Created by feliciaan on 16/06/16.
 */
public class SchamperArticlesRequest extends AbstractRequest<Articles> {

    public SchamperArticlesRequest() {
        super(Articles.class);
    }

    @NonNull
    @Override
    public String getCacheKey() {
        return "schamper.dailies";
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return ZEUS_API_URL + "1.0/schamper/daily.json";
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_HOUR * 2;
    }
}
