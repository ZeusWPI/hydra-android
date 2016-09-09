package be.ugent.zeus.hydra.requests;

import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.cache.Cache;
import be.ugent.zeus.hydra.models.association.News;
import be.ugent.zeus.hydra.requests.common.CacheableRequest;

/**
 * Request to get UGent news.
 *
 * @author feliciaan
 */
public class NewsRequest extends CacheableRequest<News> {

    public NewsRequest() {
        super(News.class);
    }

    @NonNull
    public String getCacheKey() {
        return "association_news";
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return DSA_API_URL + "all_news.json";
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_DAY;
    }
}