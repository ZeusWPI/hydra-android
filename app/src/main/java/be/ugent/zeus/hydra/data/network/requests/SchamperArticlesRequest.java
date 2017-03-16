package be.ugent.zeus.hydra.data.network.requests;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.network.caching.Cache;
import be.ugent.zeus.hydra.data.models.schamper.Articles;

/**
 * Request to get Schamper articles.
 *
 * @author feliciaan
 */
public class SchamperArticlesRequest extends be.ugent.zeus.hydra.data.network.JsonSpringRequest<Articles> implements be.ugent.zeus.hydra.data.network.caching.CacheableRequest<Articles> {

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
        return Cache.ONE_DAY;
    }
}