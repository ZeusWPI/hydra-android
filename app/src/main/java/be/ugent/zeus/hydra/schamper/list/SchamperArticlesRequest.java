package be.ugent.zeus.hydra.schamper.list;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.common.caching.Cache;
import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonSpringRequest;
import be.ugent.zeus.hydra.common.request.CacheableRequest;
import be.ugent.zeus.hydra.schamper.Article;

/**
 * Request to get Schamper articles.
 *
 * @author feliciaan
 */
public class SchamperArticlesRequest extends JsonSpringRequest<Article[]> implements CacheableRequest<Article[]> {

    public SchamperArticlesRequest() {
        super(Article[].class);
    }

    @NonNull
    @Override
    public String getCacheKey() {
        return "schamper.dailies";
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.ZEUS_API_URL_1 + "schamper/daily_android.json";
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_DAY;
    }
}