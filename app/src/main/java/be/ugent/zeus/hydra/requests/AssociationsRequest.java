package be.ugent.zeus.hydra.requests;

import com.octo.android.robospice.persistence.DurationInMillis;

import be.ugent.zeus.hydra.models.Association.Associations;

/**
 * Created by feliciaan on 04/02/16.
 */
public class AssociationsRequest extends AbstractRequest<Associations> {

    public AssociationsRequest() {
        super(Associations.class);
    }

    @Override
    public String getCacheKey() {
        return "associations.json";
    }

    @Override
    protected String getAPIUrl() {
        return DSA_API_URL + "associations.json";
    }

    @Override
    public long getCacheDuration() {
        return DurationInMillis.ONE_DAY;
    }
}
