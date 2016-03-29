package be.ugent.zeus.hydra.requests;

import com.octo.android.robospice.persistence.DurationInMillis;

import be.ugent.zeus.hydra.models.info.InfoList;

/**
 * Created by Juta on 03/03/2016.
 */
public class InfoRequest extends AbstractRequest<InfoList> {

    public InfoRequest() {
        super(InfoList.class);
    }
    @Override
    public String getCacheKey() {
        return "info-content.json";
    }

    @Override
    protected String getAPIUrl() {
        return ZEUS_API_URL + "/2.0/info/info-content.json";
    }

    @Override
    public long getCacheDuration() {
        return DurationInMillis.ONE_HOUR * 6;
    }
}
