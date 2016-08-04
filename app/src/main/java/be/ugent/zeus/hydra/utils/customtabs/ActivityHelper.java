package be.ugent.zeus.hydra.utils.customtabs;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsCallback;

import java.util.List;

/**
 * Interface for the custom tab helper.
 *
 * @author Niko Strijbol
 */
public interface ActivityHelper {

    void setIntentFlags(int flags);

    void setCallback(CustomTabsCallback callback);

    /**
     * @param uri The uri to open.
     */
    void openCustomTab(Uri uri);

    void unbindCustomTabsService(Activity activity);

    void bindCustomTabsService(Activity activity);

    boolean mayLaunchUrl(Uri uri, Bundle extras, List<Bundle> otherLikelyBundles);

    /**
     * Callback for the connection. It is not guaranteed when this is called by the ActivityHelper, so you cannot rely
     * on methods having returned yet.
     */
    interface ConnectionCallback {
        void onCustomTabsConnected(ActivityHelper helper);
        void onCustomTabsDisconnected(ActivityHelper helper);
    }
}