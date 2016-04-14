package be.ugent.zeus.hydra.requests;

import com.octo.android.robospice.persistence.DurationInMillis;

import be.ugent.zeus.hydra.models.resto.RestoMeta;

/**
 * Created by feliciaan on 04/02/16.
 */
public class RestoMetaRequest extends AbstractRequest<RestoMeta> {
    public RestoMetaRequest() {
        super(RestoMeta.class);
    }

    @Override
    public String getCacheKey() {
        return "restoMeta.json";
    }

    @Override
    protected String getAPIUrl() {
        return ZEUS_API_URL + "2.0/resto/meta.json"; //TODO: check me!
    }

    @Override
    public long getCacheDuration() {
        return DurationInMillis.ONE_HOUR * 6;
    }
}
