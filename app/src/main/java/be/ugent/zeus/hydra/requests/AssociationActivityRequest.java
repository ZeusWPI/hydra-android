package be.ugent.zeus.hydra.requests;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.springframework.http.ResponseEntity;

import be.ugent.zeus.hydra.models.AssociationActivities;
import be.ugent.zeus.hydra.models.RestoWeek;

/**
 * Created by feliciaan on 27/01/16.
 */
public class AssociationActivityRequest extends SpringAndroidSpiceRequest<AssociationActivities> {
    private static final String ACTIVITIES_API_URL = "http://student.ugent.be/hydra/api/1.0/all_activities.json";

    public AssociationActivityRequest() {
        super(AssociationActivities.class);
    }

    public AssociationActivities loadDataFromNetwork() throws Exception {
        ResponseEntity<AssociationActivities> result =  getRestTemplate().getForEntity(ACTIVITIES_API_URL, AssociationActivities.class);

        return result.getBody();
    }

    public String getCacheKey() {
        return "activities";
    }
}
