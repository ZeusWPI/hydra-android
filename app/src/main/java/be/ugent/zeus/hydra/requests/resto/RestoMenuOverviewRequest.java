package be.ugent.zeus.hydra.requests.resto;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.loader.cache.Cache;
import be.ugent.zeus.hydra.models.resto.RestoOverview;
import be.ugent.zeus.hydra.requests.common.CacheableRequest;

/**
 * CacheRequest for an overview of the resto menu.
 *
 * @author mivdnber
 */
public class RestoMenuOverviewRequest extends CacheableRequest<RestoOverview> {

    public RestoMenuOverviewRequest() {
        super(RestoOverview.class);
    }

    @NonNull
    @Override
    public String getCacheKey() {
        return "menuOverview.json";
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return ZEUS_API_URL + "2.0/resto/menu/nl/overview.json";
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_HOUR * 12;
    }
}