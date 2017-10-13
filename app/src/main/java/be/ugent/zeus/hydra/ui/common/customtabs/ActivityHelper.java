package be.ugent.zeus.hydra.ui.common.customtabs;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsCallback;

import java.util.List;

/**
 * Interface for the custom tab helper.
 *
 * @author Niko Strijbol
 */
public interface ActivityHelper {

    /**
     * Set custom intent flags.
     *
     * @param flags The flags to set.
     */
    void setIntentFlags(int flags);

    /**
     * Set a callback.
     *
     * @param callback The callback or null for no callback.
     */
    void setCallback(@Nullable CustomTabsCallback callback);

    /**
     * Launch an URL. This method's behavior is defined by the implementation: it may choose to open a custom tab or
     * open a browser, or do something else. Refer to the implementation's documentation.
     *
     * @param uri The uri to open.
     */
    void openCustomTab(Uri uri);

    /**
     * Unbind the service from the activity.
     *
     * @param activity The activity.
     */
    void unbindCustomTabsService(Activity activity);

    /**
     * Bind the service to the activity.
     *
     * @param activity The activity.
     */
    void bindCustomTabsService(Activity activity);

    /**
     * Show the default share menu or not. This is disabled by default.
     *
     */
    void setShareMenu();

    /**
     * Prime the custom tab with likely urls.
     *
     * @see android.support.customtabs.CustomTabsSession#mayLaunchUrl(Uri, Bundle, List)
     */
    boolean mayLaunchUrl(Uri uri, Bundle extras, List<Bundle> otherLikelyBundles);

    /**
     * Callback for the connection. It is not guaranteed when this is called by the ActivityHelper, so you cannot rely
     * on methods having returned yet.
     */
    interface ConnectionCallback {
        void onCustomTabsConnected(ActivityHelper helper);
        void onCustomTabsDisconnected(ActivityHelper helper);
    }

    Activity getActivity();
}