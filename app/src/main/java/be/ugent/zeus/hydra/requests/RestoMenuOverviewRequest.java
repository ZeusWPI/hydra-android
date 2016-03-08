package be.ugent.zeus.hydra.requests;

import be.ugent.zeus.hydra.models.Resto.RestoMenuList;

/**
 * Created by mivdnber on 3/5/16.
 */
public class RestoMenuOverviewRequest extends AbstractRequest<RestoMenuList> {
    public RestoMenuOverviewRequest() {
        super(RestoMenuList.class);
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
        return 0;
    }
}
