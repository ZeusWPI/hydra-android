package be.ugent.zeus.hydra.common.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.*;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import androidx.core.content.ContextCompat;

import be.ugent.zeus.hydra.R;

/**
 * Utilities related to network things.
 *
 * @author Niko Strijbol
 */
public class NetworkUtils {

    private static final String TAG = "NetworkUtils";

    /**
     * Check if the device is connected to the internet.
     *
     * @param context The context.
     *
     * @return True if there is a connection, otherwise false.
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager manager = ContextCompat.getSystemService(context, ConnectivityManager.class);
        if (manager == null) {
            return false;
        }

        if (Build.VERSION.SDK_INT < 29) {
            //noinspection deprecation
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            //noinspection deprecation
            return (networkInfo != null && networkInfo.isConnected());
        } else {
            Network network = manager.getActiveNetwork();
            NetworkCapabilities capabilities = manager.getNetworkCapabilities(network);
            if (capabilities != null) {
                return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            } else {
                return false;
            }
        }
    }

    /**
     * Check if the connection is one for which data should be restricted.
     *
     * Note that this does not check the user's settings in regards to Android 7's restricted background mode, since
     * we let the user choose what they want.
     *
     * @param context The context.
     *
     * @return True if the connection is metered.
     */
    public static boolean isMeteredConnection(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert manager != null;
        return manager.isActiveNetworkMetered();
    }

    /**
     * Opens a browser for the given url.
     *
     * @param context The context to launch from.
     * @param url     The url to launch.
     *
     * @see #maybeLaunchIntent(Context, Intent)
     */
    public static void maybeLaunchBrowser(Context context, String url) {
        maybeLaunchIntent(context, new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    /**
     * Launch an intent. This method is meant to launch implicit intents. It will also catch an error if nothing
     * can open the intent and display a toast instead.
     *
     * @param context The context to launch from.
     * @param intent  The launch intent.
     */
    public static void maybeLaunchIntent(Context context, Intent intent) {
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.w(TAG, "Launching intent failed.", e);
            Toast.makeText(context, R.string.error_no_activity, Toast.LENGTH_LONG).show();
        }
    }
}
