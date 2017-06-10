package be.ugent.zeus.hydra.data.network.requests.association;

import android.os.Bundle;
import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.data.models.association.NewsItem;
import be.ugent.zeus.hydra.data.network.Endpoints;
import be.ugent.zeus.hydra.data.network.JsonSpringRequest;
import be.ugent.zeus.hydra.data.network.caching.Cache;
import be.ugent.zeus.hydra.data.network.caching.CacheableRequest;
import be.ugent.zeus.hydra.repository.Result;
import java8.util.Comparators;

import java.util.Arrays;

/**
 * Request to get UGent news.
 *
 * @author feliciaan
 */
public class NewsRequest extends JsonSpringRequest<NewsItem[]> implements CacheableRequest<NewsItem[]> {

    public NewsRequest() {
        super(NewsItem[].class);
    }

    @NonNull
    @Override
    public Result<NewsItem[]> performRequest(Bundle args) {
        return super.performRequest(args).map(newsItems -> {
            Arrays.sort(newsItems, Comparators.reversed(Comparators.comparing(NewsItem::getDate)));
            return newsItems;
        });
    }

    @NonNull
    public String getCacheKey() {
        return "association_news";
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.DSA_API_URL_3 + "old_news.json";
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_DAY;
    }
}