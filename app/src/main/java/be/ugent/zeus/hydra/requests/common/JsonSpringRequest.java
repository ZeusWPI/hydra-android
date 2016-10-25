package be.ugent.zeus.hydra.requests.common;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Request that uses Spring and GSON to get json data from a remote location.
 *
 * @param <R> The type of the result of the request.
 *
 * @author feliciaan
 * @author Niko Strijbol
 */
public abstract class JsonSpringRequest<R> implements Request<R> {

    private Class<R> clazz;

    /**
     * @param clazz The class type of the result data.
     */
    public JsonSpringRequest(Class<R> clazz) {
        this.clazz = clazz;
    }

    /**
     * This implementation retrieves the data from the remote location using Spring and parses the result using GSON.
     * Most customisation should take place in the other functions, such as {@link #getResultType()}.
     *
     * @return The data.
     *
     * @throws RequestFailureException If the data could not be obtained, e.g. network conditions or malformed json.
     */
    @NonNull
    @Override
    public R performRequest() throws RequestFailureException {
        try {
            return createRestTemplate().getForEntity(getAPIUrl(), getResultType()).getBody();
        } catch (RestClientException e) {
            throw new RequestFailureException(e);
        }
    }

    @NonNull
    private Class<R> getResultType() {
        return clazz;
    }

    @NonNull
    protected abstract String getAPIUrl();

    /**
     * @return The rest template used by Spring to perform the request.
     *
     * @throws be.ugent.zeus.hydra.requests.exceptions.RestTemplateException If something went wrong while creating the rest template.
     */
    protected RestTemplate createRestTemplate() throws RequestFailureException {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
        return restTemplate;
    }
}