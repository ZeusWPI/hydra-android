/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra.common.network;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.annotation.WorkerThread;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.UnknownServiceException;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import be.ugent.zeus.hydra.common.arch.data.BaseLiveData;
import be.ugent.zeus.hydra.common.request.Result;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import okhttp3.CacheControl;
import okhttp3.Response;

/**
 * Common implementation base for requests that are network requests. This request provides built-in caching on the
 * networking level.
 *
 * <h1>Caching</h1>
 * The caching implementation uses OkHttp's cache implementation. How long a response is cached is determined by
 * {@link #getCacheDuration()}. By default, requests are not cached.
 * <p>
 * Additionally, stale data is always used by default if needed.
 * <p>
 * To disable the cache, pass {@link BaseLiveData#REFRESH_COLD} as an argument to the request.
 *
 * <h1>Decode</h1>
 * The request uses Moshi to decode the json response into Java objects.
 *
 * @author Niko Strijbol
 */
@SuppressWarnings("WeakerAccess")
public abstract class JsonOkHttpRequest<D> extends OkHttpRequest<D> {

    private static final String TAG = "JsonOkHttpRequest";

    private static final String ALLOW_STALENESS = "be.ugent.zeus.hydra.data.staleness";

    private final Type typeToken;

    /**
     * Construct a new request. As this constructor is not type-safe, it must only be used internally.
     *
     * @param context The context.
     * @param token   The type token of the return type.
     */
    JsonOkHttpRequest(@NonNull Context context, @NonNull Type token) {
        super(context);
        this.typeToken = token;
    }

    /**
     * Construct a new request.
     *
     * @param context The context.
     * @param token   The class of the return type. If you need a generic list, use {@link JsonArrayRequest} instead.
     */
    public JsonOkHttpRequest(@NonNull Context context, @NonNull Class<D> token) {
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
            return executeRequest(adapter, args);
        } catch (IOException e) {

            // If this exception is for a clear text violation, log it. We want to fix these.
            if (e instanceof UnknownServiceException) {
                Log.e(TAG, "Unexpected error during network request.", e);
                tracker.logError(e);
            }

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
    }

    protected JsonAdapter<D> getAdapter() {
        return moshi.adapter(typeToken);
    }

    protected Result<D> executeRequest(JsonAdapter<D> adapter, @NonNull Bundle args) throws IOException {
        okhttp3.Request request = constructRequest(args).build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new UnsuccessfulRequestException(response.code());
            }

            if (response.body() == null) {
                throw new NullPointerException("Unexpected null body on request response.");
            }

            D result = adapter.fromJson(Objects.requireNonNull(response.body()).source());

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
            tracker.logError(exception);
            return Result.Builder.fromException(exception);
        }
    }

    @NonNull
    protected abstract String getAPIUrl();

    protected okhttp3.Request.Builder constructRequest(@NonNull Bundle arguments) {
        return new okhttp3.Request.Builder()
                .url(getAPIUrl())
                .cacheControl(constructCacheControl(arguments))
                .addHeader("Accept", "application/json");
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
}
