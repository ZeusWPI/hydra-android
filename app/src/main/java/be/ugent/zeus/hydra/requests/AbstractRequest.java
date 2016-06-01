package be.ugent.zeus.hydra.requests;

import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.loader.cache.Request;
import be.ugent.zeus.hydra.loader.cache.exceptions.RequestFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.Map;

/**
 * Abstract request class.
 *
 * @param <T> The type of the result of the request.
 *
 * @author feliciaan
 */
public abstract class AbstractRequest<T extends Serializable> implements Request<T> {

    protected final String DSA_API_URL = "http://student.ugent.be/hydra/api/2.0/";
    protected final String ZEUS_API_URL = "https://zeus.UGent.be/hydra/api/";

    private Class<T> clazz;

    public AbstractRequest(Class<T> clazz) {
        this.clazz = clazz;
    }

    @NonNull
    @Override
    public T performRequest() throws RequestFailureException {
        RestTemplate restTemplate = createRestTemplate();
        ResponseEntity<T> result;

        try {

            if (getURLVariables() == null) {
                result = restTemplate.getForEntity(getAPIUrl(), getResultType());
            } else {
                result = restTemplate.getForEntity(getAPIUrl(), getResultType(), getURLVariables());
            }
        } catch (RestClientException e) {
            throw new RequestFailureException(e);
        }

        return result.getBody();
    }

    @NonNull
    public Class<T> getResultType() {
        return clazz;
    }

    @NonNull
    public abstract String getCacheKey();

    @NonNull
    protected abstract String getAPIUrl();

    public abstract long getCacheDuration();

    protected Map<String, String> getURLVariables() {
        return null;
    }

    private RestTemplate createRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
        return restTemplate;
    }
}