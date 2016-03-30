package be.ugent.zeus.hydra.requests;

import com.octo.android.robospice.persistence.DurationInMillis;

import be.ugent.zeus.hydra.models.association.AssociationActivities;

/**
 * Created by feliciaan on 27/01/16.
 */
public class AssociationActivitiesRequest extends AbstractRequest<AssociationActivities> {

    public AssociationActivitiesRequest() {
        super(AssociationActivities.class);
    }

    public String getCacheKey() {
        return "all_activities.json";
    }

    @Override
    protected String getAPIUrl() {
        return DSA_API_URL + "all_activities.json";
    }

    @Override
    public long getCacheDuration() {
        return DurationInMillis.ONE_MINUTE * 15;
    }
}
