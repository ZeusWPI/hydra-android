package be.ugent.zeus.hydra.requests;

import be.ugent.zeus.hydra.models.resto.RestoOverview;
import com.octo.android.robospice.persistence.DurationInMillis;

/**
 * Request for an overview of the resto menu.
 *
 * @author mivdnber
 */
public class RestoMenuOverviewRequest extends AbstractRequest<RestoOverview> {

    public RestoMenuOverviewRequest() {
        super(RestoOverview.class);
    }

    @Override
    public String getCacheKey() {
        return "menuOverview.json";
    }

    @Override
    protected String getAPIUrl() {
        return ZEUS_API_URL + "2.0/resto/menu/nl/overview.json";
    }

    @Override
    public long getCacheDuration() {
        return DurationInMillis.ONE_HOUR * 12;
    }
}