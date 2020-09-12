package be.ugent.zeus.hydra.testing;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Prevent network access during tests.
 * <p>
 * TODO: maybe use something like OkReplay in the future)
 *
 * @author Niko Strijbol
 */
public class NoNetworkInterceptor implements Interceptor {
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        throw new IOException("No network during tests!");
    }
}
