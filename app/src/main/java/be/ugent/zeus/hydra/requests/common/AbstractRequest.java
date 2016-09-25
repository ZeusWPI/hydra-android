package be.ugent.zeus.hydra.requests.common;

import android.support.annotation.NonNull;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Abstract request class.
 *
 * @param <T> The type of the result of the request.
 *
 * @author feliciaan
 * @author Niko Strijbol
 */
public abstract class AbstractRequest<T> implements Request<T> {

    private Class<T> clazz;

    public AbstractRequest(Class<T> clazz) {
        this.clazz = clazz;
    }

    @NonNull
    @Override
    public T performRequest() throws RequestFailureException {
        try {
            RestTemplate restTemplate = createRestTemplate();
            ResponseEntity<T> result;

            if (getURLVariables() == null) {
                result = restTemplate.getForEntity(getAPIUrl(), getResultType());
            } else {
                result = restTemplate.getForEntity(getAPIUrl(), getResultType(), getURLVariables());
            }

            return result.getBody();
        } catch (RestClientException | RestTemplateException e) {
            throw new RequestFailureException(e);
        }
    }

    @NonNull
    public Class<T> getResultType() {
        return clazz;
    }

    @NonNull
    protected abstract String getAPIUrl();

    protected Map<String, String> getURLVariables() {
        return null;
    }

    /**
     * @return The rest template used by Spring to perform the request.
     */
    protected RestTemplate createRestTemplate() throws RestTemplateException {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
        return restTemplate;
    }
}