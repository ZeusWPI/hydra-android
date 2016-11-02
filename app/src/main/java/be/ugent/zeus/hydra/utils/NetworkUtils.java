package be.ugent.zeus.hydra.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
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
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
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
        return manager.isActiveNetworkMetered();
    }

    public static void maybeLaunchBrowser(Context context, String url) {
        maybeLaunchIntent(context, new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    public static void maybeLaunchIntent(Context context, Intent intent) {
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.w(TAG, "Launching intent failed.", e);
            Toast.makeText(context, R.string.error_no_activity, Toast.LENGTH_LONG).show();
        }
    }
}