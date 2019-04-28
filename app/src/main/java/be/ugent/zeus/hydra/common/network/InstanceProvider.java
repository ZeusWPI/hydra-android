package be.ugent.zeus.hydra.common.network;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.VisibleForTesting;

import java.io.File;

import be.ugent.zeus.hydra.common.converter.BooleanJsonAdapter;
import be.ugent.zeus.hydra.common.converter.DateThreeTenAdapter;
import be.ugent.zeus.hydra.common.converter.DateTypeConverters;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.squareup.moshi.Moshi;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Provide instances of singletons. In the future this might be replaced with dependency injection.
 *
 * @author Niko Strijbol
 */
public final class InstanceProvider {

    private static final String TAG = "InstanceProvider";

    private static OkHttpClient client;

    private static final long CACHE_SIZE = 20 * 1024 * 1024; // 20 MiB

    private InstanceProvider() {
    }

    private static synchronized OkHttpClient getClient(File cacheDir) {
        if (client == null) {
            client = getBuilder(cacheDir).build();
        }
        return client;
    }

    @VisibleForTesting
    public static OkHttpClient.Builder getBuilder(File cacheDir) {
        return new OkHttpClient.Builder().cache(new Cache(cacheDir, CACHE_SIZE));
    }

    /**
     * Get the OkHttpClient we use for requests.
     *
     * @param context A context.
     *
     * @return The client.
     */
    public static synchronized OkHttpClient getClient(Context context) {

        if (Build.VERSION.SDK_INT <= 20) {
            installGoogleProvider(context);
        }

        File cacheDir = new File(context.getCacheDir(), "http");
        return getClient(cacheDir);
    }

    private static Moshi moshi;

    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
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

    private static void installGoogleProvider(Context context) {
        Log.i(TAG, "Installing Play Services to enable TLSv1.2");
        try {
            ProviderInstaller.installIfNeeded(context);
        } catch (GooglePlayServicesRepairableException e) {
            Log.w(TAG, "Play Services are outdated", e);
            // Prompt the user to install/update/enable Google Play services.
            GoogleApiAvailability.getInstance().showErrorNotification(context, e.getConnectionStatusCode());
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, "Unable to install provider, SSL will not work", e);
        }
    }
}