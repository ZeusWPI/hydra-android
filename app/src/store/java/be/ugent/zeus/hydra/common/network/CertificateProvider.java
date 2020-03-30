package be.ugent.zeus.hydra.common.network;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

/**
 * @author Niko Strijbol
 */
public class CertificateProvider {

    private static final String TAG = "CertificateProvider";

    public static void installProvider(Context context) {
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
