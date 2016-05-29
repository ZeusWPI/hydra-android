package be.ugent.zeus.hydra.requests;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * Abstract request class.
 *
 * @param <T> The type of the result of the request.
 *
 * @author feliciaan
 */
public abstract class AbstractRequest<T> extends SpringAndroidSpiceRequest<T> {

    protected final String DSA_API_URL = "http://student.ugent.be/hydra/api/2.0/";
    protected final String ZEUS_API_URL = "https://zeus.UGent.be/hydra/api/";

    public AbstractRequest(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public T loadDataFromNetwork() throws Exception {
        ResponseEntity<T> result;
        if (getURLVariables() == null) {
            result = getRestTemplate().getForEntity(getAPIUrl(), getResultType());
        } else {
            result = getRestTemplate().getForEntity(getAPIUrl(), getResultType(), getURLVariables());
        }
        return result.getBody();
    }

    public abstract String getCacheKey();

    protected abstract String getAPIUrl();

    public abstract long getCacheDuration();

    protected Map<String, String> getURLVariables() {
        return null;
    }
}
