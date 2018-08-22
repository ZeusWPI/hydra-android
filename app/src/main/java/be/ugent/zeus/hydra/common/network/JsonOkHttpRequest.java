package be.ugent.zeus.hydra.common.network;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.annotation.WorkerThread;
import android.util.Log;

import be.ugent.zeus.hydra.common.arch.data.BaseLiveData;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.RequestException;
import be.ugent.zeus.hydra.common.request.Result;
import com.crashlytics.android.Crashlytics;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.threeten.bp.Duration;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

/**
 * Common implementation base for requests that are network requests. This request provides built-in caching on the
 * networking level.
 *
 * <h1>Caching</h1>
 * The caching implementation uses OkHttp's cache implementation. How long a response is cached is determined by
 * {@link #getCacheDuration()}. By default, requests are not cached.
 *
 * Additionally, stale data is always used by default if needed.
 *
 * To disable the cache, pass {@link BaseLiveData#REFRESH_COLD} as an argument to the request.
 *
 * <h1>Decode</h1>
 * The request uses Moshi to decode the json response into Java objects.
 *
 * @author Niko Strijbol
 */
public abstract class JsonOkHttpRequest<D> implements Request<D> {

    private static final String TAG = "JsonOkHttpRequest";

    private static final String ALLOW_STALENESS = "be.ugent.zeus.hydra.data.staleness";

    private final Moshi moshi;
    private final OkHttpClient client;
    private final Type typeToken;

    /**
     * Construct a new request. As this constructor is not type-safe, it must only be used internally.
     *
     * @param context The context.
     * @param token The type token of the return type.
     */
    JsonOkHttpRequest(Context context, Type token) {
        this.moshi = InstanceProvider.getMoshi();
        this.client = InstanceProvider.getClient(context);
        this.typeToken = token;
    }

    /**
     * Construct a new request.
     *
     * @param context The context.
     * @param token   The class of the return type. If you need a generic list, use {@link JsonArrayRequest} instead.
     */
    public JsonOkHttpRequest(Context context, Class<D> token) {
        this(context, (Type) token);
    }

    /**
     * Execute the request. See the class documentation for a description of the caching system.
     */
    @NonNull
    @Override
    @WorkerThread
    public Result<D> execute(@NonNull Bundle args) {

        JsonAdapter<D> adapter = getAdapter();

        try {
            try {
                return executeRequest(adapter, args);
            } catch (IOException e) {

                Result<D> result = Result.Builder.fromException(new IOFailureException(e));

                // Only do this if caching is enabled.
                if (constructCacheControl(args) == CacheControl.FORCE_NETWORK) {
                    Log.d(TAG, "Cache is disabled, do not attempt getting stale data.");
                    return result;
                }

                Log.d(TAG, "Error while getting data, try to get stale data.", e);
                // We try to get stale data at this point.
                args = new Bundle(args);
                args.putBoolean(ALLOW_STALENESS, true);

                try {
                    Result<D> staleResult = executeRequest(adapter, args);
                    Log.d(TAG, "Stale data was found and used.");
                    // Add the result.
                    return result.updateWith(staleResult);
                } catch (IOException e2) {
                    Log.d(TAG, "Stale data was not found.", e2);
                    // Just give up at this point.
                    return result;
                }
            }
        } catch (ConstructionException e) {
            return Result.Builder.fromException(new RequestException(e));
        }
    }

    protected JsonAdapter<D> getAdapter() {
        return moshi.adapter(typeToken);
    }

    protected Result<D> executeRequest(JsonAdapter<D> adapter, @NonNull Bundle args) throws IOException, ConstructionException {
        okhttp3.Request request = constructRequest(args).build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new UnsuccessfulRequestException(response.code());
        }

        assert response.body() != null;

        try {
            D result = adapter.fromJson(response.body().source());
            if (result == null) {
                throw new NullPointerException("Null is not a valid value.");
            }
            return new Result.Builder<D>()
                    .withData(result)
                    .build();
        } catch (JsonDataException | NullPointerException e) {
            // Create, log and throw exception, since this is not normal.
            String message = "The server did not respond with the expected format for URL: " + getAPIUrl();
            InvalidFormatException exception = new InvalidFormatException(message, e);
            Crashlytics.logException(exception);
            return Result.Builder.fromException(exception);
        }
    }

    @NonNull
    protected abstract String getAPIUrl();

    protected okhttp3.Request.Builder constructRequest(@NonNull Bundle arguments) throws ConstructionException {
        return new okhttp3.Request.Builder()
                .url(getAPIUrl())
                .cacheControl(constructCacheControl(arguments));
    }

    protected CacheControl constructCacheControl(@NonNull Bundle arguments) {
        CacheControl.Builder cacheControl = new CacheControl.Builder();

        // TODO: we can simplify this is OkHttp supports the stale-if-error header.
        // Track it at https://github.com/square/okhttp/issues/1083.
        if (arguments.getBoolean(ALLOW_STALENESS, false)) {
            Log.d(TAG, "constructCacheControl: stale data is allowed!");
            cacheControl.maxStale(Integer.MAX_VALUE, TimeUnit.SECONDS);
        }

        // If the REFRESH_COLD argument is set, ignore the cache duration and set it to zero, otherwise use the
        // duration provided by the request. By using this method, in contrast to noCache(), allows the stale data to
        // be used if the network failed for some reason.
        if (arguments.getBoolean(BaseLiveData.REFRESH_COLD, false)) {
            cacheControl.maxAge(0, TimeUnit.SECONDS);
        } else {
            Duration cacheDuration = getCacheDuration();
            cacheControl.maxAge((int) cacheDuration.getSeconds(), TimeUnit.SECONDS);
        }
        return cacheControl.build();
    }

    /**
     * How long the result of this request should be cached. By default, things are not cached.
     *
     * @return The duration, 0 by default.
     */
    protected Duration getCacheDuration() {
        return Duration.ZERO;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    Type getTypeToken() {
        return typeToken;
    }

    /**
     * Exception thrown when the construction of the request failed for some reason.
     */
    protected static class ConstructionException extends Exception {
        public ConstructionException(String message) {
            super(message);
        }
    }
}