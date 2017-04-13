package be.ugent.zeus.hydra.service.urgent;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;

/**
 * Common util methods to work with services.
 *
 * @author Niko Strijbol
 */
public class ServiceUtils {

    /**
     * Check if a given service is running.
     *
     * @see <a href="http://stackoverflow.com/a/5921190/1831741">stack overflow</a>
     *
     * @param context A context.
     * @param serviceClass The class of the service you want to check.
     * @return True if the service is running, otherwise false.
     */
    public static boolean isMyServiceRunning(Context context, Class<? extends Service> serviceClass) {

        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }
}