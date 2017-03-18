package be.ugent.zeus.hydra.data.network.requests;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.models.association.NewsItem;
import be.ugent.zeus.hydra.data.network.Endpoints;
import be.ugent.zeus.hydra.data.network.JsonSpringRequest;
import be.ugent.zeus.hydra.data.network.caching.Cache;
import be.ugent.zeus.hydra.data.network.caching.CacheableRequest;
import be.ugent.zeus.hydra.data.network.exceptions.RequestFailureException;
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
    public NewsItem[] performRequest() throws RequestFailureException {
        NewsItem[] data = super.performRequest();
        Arrays.sort(data, Comparators.reversed(Comparators.comparing(NewsItem::getDate)));
        return data;
    }

    @NonNull
    public String getCacheKey() {
        return "association_news";
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.DSA_API_URL + "all_news.json";
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_DAY;
    }
}