package be.ugent.zeus.hydra.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.Nullable;

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

}