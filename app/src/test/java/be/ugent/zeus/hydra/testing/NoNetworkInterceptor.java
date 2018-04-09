package be.ugent.zeus.hydra.testing;

import okhttp3.Interceptor;
import okhttp3.Response;

import java.io.IOException;

/**
 * Prevent network access during tests.
 *
 * TODO: maybe use something like OkReplay in the future)
 *
 * @author Niko Strijbol
 */
public class NoNetworkInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        throw new IOException("No network during tests!");
    }
}