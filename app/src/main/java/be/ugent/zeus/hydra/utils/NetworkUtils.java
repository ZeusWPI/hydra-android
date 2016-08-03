package be.ugent.zeus.hydra.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.customtabs.*;

/**
 * Utils for working with the network.
 *
 * @author Niko Strijbol
 */
public class NetworkUtils {

    private static final String EDUROAM = "eduroam";

    /**
     * @return The name of the WiFi-network or null if not connected.
     */
    @Nullable
    public static String getWifiName(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (manager.isWifiEnabled()) {
            WifiInfo info = manager.getConnectionInfo();
            return info.getSSID();
        }
        return null;
    }

    /**
     * Try and guess if the user is on the network of the Ugent or not.
     *
     * This does currently not check for VPN connections, but it could in the future. This function is only a guess.
     *
     * @param context The context.
     *
     * @return True if connected to Ugent, otherwise false.
     */
    public static boolean isUgentNetwork(Context context) {
        String name = getWifiName(context);

        return EDUROAM.equals(name);
    }

    public static void launchCustomTabOrBrowser(String url, Activity context) {
        //If possible, use custom tabs
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {

            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();

            //Get theme color
            builder.setToolbarColor(ViewUtils.getPrimaryColor(context));
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            customTabsIntent.launchUrl(context, Uri.parse(url));
        } else {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            browserIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            context.startActivity(browserIntent);
        }
    }
}