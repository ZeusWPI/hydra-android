package be.ugent.zeus.hydra.common.network;

import android.support.annotation.NonNull;

import org.springframework.web.client.RestTemplate;

/**
 * Dummy implementations for the request.
 *
 * @author Niko Strijbol
 */
@Deprecated
public class TestRequest<R> extends JsonSpringRequest<R> {

    private static final String URL = "http://www.example.com/test";

    private TestRequest(Class<R> clazz) {
        super(clazz);
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return URL;
    }

    /**
     * Create a dummy request for a certain type.
     *
     * @param clazz The class of the type.
     * @param <R>   The type.
     *
     * @return The request.
     */
    public static <R> JsonSpringRequest<R> forType(Class<R> clazz) {
        return new TestRequest<>(clazz);
    }

    /**
     * Create a dummy request when you don't care what type.
     *
     * @return The request.
     */
    public static JsonSpringRequest<?> any() {
        return new TestRequest<>(Object.class);
    }

    /**
     * Create a dummy request that will throw an exception when {@link #createRestTemplate()} is called.
     *
     * @return The request.
     */
    public static JsonSpringRequest<?> problematicRestTemplate() {
        return new TestRequest<Object>(Object.class) {
            @Override
            protected RestTemplate createRestTemplate() throws RestTemplateException {
                throw new RestTemplateException(new RuntimeException("Hello"));
            }
        };
    }
}