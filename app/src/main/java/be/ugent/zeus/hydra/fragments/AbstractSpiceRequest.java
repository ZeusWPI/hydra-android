package be.ugent.zeus.hydra.fragments;

/**
 * @author Niko Strijbol
 * @version 4/07/2016
 */

import be.ugent.zeus.hydra.loader.cache.Request;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by feliciaan on 04/02/16.
 */
public abstract class AbstractSpiceRequest<T extends Serializable> extends SpringAndroidSpiceRequest<T> implements Request<T> {

    protected Class jsonClass;

    protected final String DSA_API_URL = "http://student.ugent.be/hydra/api/2.0/";
    protected final String ZEUS_API_URL = "https://zeus.UGent.be/hydra/api/";

    public AbstractSpiceRequest(Class<T> clazz) {
        super(clazz);
        jsonClass = clazz;
    }

    public T loadDataFromNetwork() throws Exception {
        ResponseEntity<T> result;
        if (getURLVariables() == null) {
            result = getRestTemplate().getForEntity(getAPIUrl(), jsonClass);
        } else {
            result = getRestTemplate().getForEntity(getAPIUrl(), jsonClass, getURLVariables());
        }
        return result.getBody();
    }

    public abstract String getCacheKey();

    protected abstract String getAPIUrl();

    public abstract long getCacheDuration();

    protected Map<String, String> getURLVariables() {
        return null;
    }

    public void execute(SpiceManager spiceManager, RequestListener<T> listener) {
        spiceManager.execute(this, this.getCacheKey(), this.getCacheDuration(), listener);
    }
}
