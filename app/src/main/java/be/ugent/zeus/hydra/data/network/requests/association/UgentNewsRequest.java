package be.ugent.zeus.hydra.data.network.requests.association;

import android.os.Bundle;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.models.association.UgentNewsItem;
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
public class UgentNewsRequest extends JsonSpringRequest<UgentNewsItem[]> implements CacheableRequest<UgentNewsItem[]> {

    public UgentNewsRequest() {
        super(UgentNewsItem[].class);
    }

    @NonNull
    @Override
    public UgentNewsItem[] performRequest(Bundle args) throws RequestFailureException {
        UgentNewsItem[] data = super.performRequest(args);
        Arrays.sort(data, Comparators.reversed(Comparators.comparing(UgentNewsItem::getModified)));
        return data;
    }

    @NonNull
    public String getCacheKey() {
        return "ugent_association_news";
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.DSA_API_URL_3 + "recent_news.json";
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_DAY;
    }
}