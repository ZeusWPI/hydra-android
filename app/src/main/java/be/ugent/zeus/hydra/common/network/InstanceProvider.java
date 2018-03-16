package be.ugent.zeus.hydra.common.network;

import android.content.Context;

import be.ugent.zeus.hydra.common.converter.BooleanJsonAdapter;
import be.ugent.zeus.hydra.common.converter.DateThreeTenAdapter;
import be.ugent.zeus.hydra.common.converter.ZonedThreeTenAdapter;
import com.google.gson.Gson;
import com.squareup.moshi.Moshi;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Provide instances of singletons. In the future this might be replaced with dependency injection.
 *
 * @author Niko Strijbol
 */
class InstanceProvider {

    private static OkHttpClient client;

    private static final long CACHE_SIZE = 10 * 1024 * 1024;

    public static synchronized OkHttpClient getClient(Context context) {
        if (client == null) {
            client = new OkHttpClient.Builder()
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .cache(new Cache(context.getCacheDir(), CACHE_SIZE)) // 10 MiB cache
                    .build();
        }
        return client;
    }

    private static Gson gson;

    public static synchronized Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    private static Moshi moshi;

    public static synchronized Moshi getMoshi() {
        if (moshi == null) {
            moshi = new Moshi.Builder()
                    .add(new BooleanJsonAdapter())
                    .add(new ZonedThreeTenAdapter())
                    .add(new DateThreeTenAdapter())
                    .build();
        }
        return moshi;
    }
}