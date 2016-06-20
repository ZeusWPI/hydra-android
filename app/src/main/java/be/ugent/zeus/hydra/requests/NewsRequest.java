package be.ugent.zeus.hydra.requests;

import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.loader.cache.Cache;
import be.ugent.zeus.hydra.models.association.News;

/**
 * Created by feliciaan on 04/02/16.
 */
public class NewsRequest extends AbstractRequest<News> {

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
