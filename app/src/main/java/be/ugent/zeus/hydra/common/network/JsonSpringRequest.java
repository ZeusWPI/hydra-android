package be.ugent.zeus.hydra.common.network;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.RequestException;
import be.ugent.zeus.hydra.common.request.Result;
import com.google.firebase.crash.FirebaseCrash;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.ResourceAccessException;
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
    private RestTemplate restTemplate;

    /**
     * @param clazz The class type of the result data.
     */
    public JsonSpringRequest(Class<R> clazz) {
        this.clazz = clazz;
    }

    /**
     * This implementation retrieves the data from the remote location using Spring and parses the result using GSON.
     *
     * @return The data.
     */
    @NonNull
    @Override
    public Result<R> performRequest(Bundle args) {
        try {
            // Note: if you are chasing a bug here, take a look at b/112443.
            // Apparently, this crashes sometimes on some Samsung devices, but there is nothing we can do.
            R result = getRestTemplate().getForEntity(getAPIUrl(), clazz).getBody();
            if (result == null) {
                // Create, log and throw exception, since this is not normal.
                ResourceAccessException exception = new ResourceAccessException("The server did not respond with the expected format for URL: " + getAPIUrl());
                FirebaseCrash.report(exception);
                throw exception;
            }
            return new Result.Builder<R>()
                    .withData(result)
                    .build();
        } catch (ResourceAccessException e) {
            return new Result.Builder<R>()
                    .withError(new IOFailureException(e))
                    .build();
        } catch (RestClientException e) {
            return new Result.Builder<R>()
                    .withError(new RequestException(e))
                    .build();
        } catch (HttpMessageConversionException e) {
            // We log the wrapping exception in Firebase to be able to view the URL of the failing request.
            RequestException wrapping = new RequestException("Could not read JSON for " + getAPIUrl(), e);
            //FirebaseCrash.report(wrapping);
            return new Result.Builder<R>()
                    .withError(wrapping)
                    .build();
        } catch (RestTemplateException e) {
            return new Result.Builder<R>()
                    .withError(e)
                    .build();
        }
    }

    @NonNull
    protected abstract String getAPIUrl();

    /**
     * @return The rest template used by Spring to perform the request.
     */
    protected RestTemplate createRestTemplate() throws RestTemplateException {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
        return restTemplate;
    }

    @VisibleForTesting
    public RestTemplate getRestTemplate() throws RestTemplateException {
        if (restTemplate == null) {
            restTemplate = createRestTemplate();
        }
        return restTemplate;
    }
}