package be.ugent.zeus.hydra.requests;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.springframework.http.ResponseEntity;

import java.util.Map;

import be.ugent.zeus.hydra.models.AssociationNews;

/**
 * Created by feliciaan on 04/02/16.
 */
public abstract class AbstractRequest<T> extends SpringAndroidSpiceRequest<T> {

    protected Class jsonClass;

    protected final String DSA_API_URL = "http://student.ugent.be/hydra/api/1.0/";
    protected final String ZEUS_API_URL = "http://zeus.UGent.be/hydra/api/";

    public AbstractRequest(Class clazz) {
        super(clazz);
        jsonClass = clazz;
    }

    public T loadDataFromNetwork() throws Exception {
        ResponseEntity<T> result =  getRestTemplate().getForEntity(getAPIUrl(), jsonClass, getURLVariables());

        return result.getBody();
    }

    public abstract String getCacheKey();
    protected abstract String getAPIUrl();
    public abstract long getCacheDuration();

    protected Map<String, String> getURLVariables() {
        return null;
    }
}
