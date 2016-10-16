package be.ugent.zeus.hydra.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Utilities related to network things.
 *
 * @author Niko Strijbol
 */
public class NetworkUtils {

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
}