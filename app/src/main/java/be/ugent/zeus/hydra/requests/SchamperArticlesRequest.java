package be.ugent.zeus.hydra.requests;

import com.octo.android.robospice.persistence.DurationInMillis;

import be.ugent.zeus.hydra.models.schamper.Articles;

/**
 * Created by feliciaan on 16/06/16.
 */
public class SchamperArticlesRequest extends AbstractRequest<Articles> {

    public SchamperArticlesRequest() {
        super(Articles.class);
    }

    @Override
    public String getCacheKey() {
        return "schamper.dailies";
    }

    @Override
    protected String getAPIUrl() {
        return ZEUS_API_URL + "1.0/schamper/daily.json";
    }

    @Override
    public long getCacheDuration() {
        return DurationInMillis.ONE_HOUR * 2;
    }
}
