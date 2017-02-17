package be.ugent.zeus.hydra.requests;

import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.caching.Cache;
import be.ugent.zeus.hydra.models.association.News;
import be.ugent.zeus.hydra.requests.common.CacheableRequest;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;
import java8.util.Lists;

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
    @Override
    public News performRequest() throws RequestFailureException {
        News unsorted = super.performRequest();
        Lists.sort(unsorted, (o1, o2) -> -o1.getDate().compareTo(o2.getDate()));
        return unsorted;
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