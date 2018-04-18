package be.ugent.zeus.hydra.common.network;

import android.content.Context;
import android.support.annotation.VisibleForTesting;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.common.converter.BooleanJsonAdapter;
import be.ugent.zeus.hydra.common.converter.DateThreeTenAdapter;
import be.ugent.zeus.hydra.common.converter.DateTypeConverters;
import com.squareup.moshi.Moshi;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.File;

/**
 * Provide instances of singletons. In the future this might be replaced with dependency injection.
 *
 * @author Niko Strijbol
 */
@VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
public class InstanceProvider {

    private static OkHttpClient client;

    private static final long CACHE_SIZE = 10 * 1024 * 1024; // 10 MiB

    public static synchronized OkHttpClient getClient(File cacheDir) {
        if (client == null) {
            OkHttpClient.Builder builder = getBuilder(cacheDir);

            client = builder.build();
        }
        return client;
    }

    @VisibleForTesting
    public static OkHttpClient.Builder getBuilder(File cacheDir) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder().cache(new Cache(cacheDir, CACHE_SIZE));

        if (BuildConfig.DEBUG && BuildConfig.DEBUG_NETWORK_ENABLE_LOGS) {
            builder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        }

        return builder;
    }

    public static synchronized OkHttpClient getClient(Context context) {
        File cacheDir = new File(context.getCacheDir(), "http");
        return getClient(cacheDir);
    }

    private static Moshi moshi;

    public static synchronized Moshi getMoshi() {
        if (moshi == null) {
            moshi = new Moshi.Builder()
                    .add(new BooleanJsonAdapter())
                    .add(new DateThreeTenAdapter())
                    .add(new DateTypeConverters.GsonOffset())
                    .add(new DateTypeConverters.LocalZonedDateTimeInstance())
                    .build();
        }
        return moshi;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public static void reset() {
        client = null;
        moshi = null;
    }
}