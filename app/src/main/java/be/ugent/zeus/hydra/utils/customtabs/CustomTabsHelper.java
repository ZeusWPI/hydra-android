package be.ugent.zeus.hydra.utils.customtabs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.support.customtabs.CustomTabsService;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper for custom tabs.
 *
 * Taken from https://github.com/hitherejoe/Tabby
 *
 * @author Niko Strijbol
 */
public class CustomTabsHelper {

    private static final String TAG = "CustomTabsHelper";

    //Chrome packages.
    static final String PACKAGE_STABLE = "com.android.chrome";
    static final String PACKAGE_BETA = "com.chrome.beta";
    static final String PACKAGE_DEV = "com.chrome.dev";
    static final String PACKAGE_LOCAL = "com.google.android.apps.chrome";

    private static String packageNameToUse;

    /**
     * Goes through all apps that handle VIEW intents and have a warm up service. Picks
     * the one chosen by the user if there is one, otherwise makes a best effort to return a
     * valid package name.
     *
     * This is <strong>not</strong> threadsafe.
     *
     * @param context {@link Context} to use for accessing {@link PackageManager}.
     * @return The package name recommended to use for connecting to custom tabs related components.
     */
    public static String getPackageNameToUse(Context context) {

        if (packageNameToUse != null) {
            return packageNameToUse;
        }

        PackageManager pm = context.getPackageManager();
        // Get default VIEW intent handler.
        Intent activityIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com"));
        ResolveInfo defaultViewHandlerInfo = pm.resolveActivity(activityIntent, 0);
        String defaultViewHandlerPackageName = null;
        if (defaultViewHandlerInfo != null) {
            defaultViewHandlerPackageName = defaultViewHandlerInfo.activityInfo.packageName;
        }

        // Get all apps that can handle VIEW intents.
        List<ResolveInfo> resolvedActivityList = pm.queryIntentActivities(activityIntent, 0);
        List<String> packagesSupportingCustomTabs = new ArrayList<>();
        for (ResolveInfo info : resolvedActivityList) {
            Intent serviceIntent = new Intent();
            serviceIntent.setAction(CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION);
            serviceIntent.setPackage(info.activityInfo.packageName);
            if (pm.resolveService(serviceIntent, 0) != null) {
                packagesSupportingCustomTabs.add(info.activityInfo.packageName);
            }
        }

        // Now packagesSupportingCustomTabs contains all apps that can handle both VIEW intents
        // and service calls.
        if (packagesSupportingCustomTabs.isEmpty()) {
            packageNameToUse = null;
        } else if (packagesSupportingCustomTabs.size() == 1) {
            packageNameToUse = packagesSupportingCustomTabs.get(0);
        } else if (!TextUtils.isEmpty(defaultViewHandlerPackageName)
                && !hasSpecializedHandlerIntents(context, activityIntent)
                && packagesSupportingCustomTabs.contains(defaultViewHandlerPackageName)) {
            packageNameToUse = defaultViewHandlerPackageName;
        } else if (packagesSupportingCustomTabs.contains(PACKAGE_STABLE)) {
            packageNameToUse = PACKAGE_STABLE;
        } else if (packagesSupportingCustomTabs.contains(PACKAGE_BETA)) {
            packageNameToUse = PACKAGE_BETA;
        } else if (packagesSupportingCustomTabs.contains(PACKAGE_DEV)) {
            packageNameToUse = PACKAGE_DEV;
        } else if (packagesSupportingCustomTabs.contains(PACKAGE_LOCAL)) {
            packageNameToUse = PACKAGE_LOCAL;
        }
        return packageNameToUse;
    }

    /**
     * Used to check whether there is a specialized handler for a given intent.
     *
     * @param intent The intent to check with.
     *
     * @return Whether there is a specialized handler for the given intent.
     */
    private static boolean hasSpecializedHandlerIntents(Context context, Intent intent) {
        try {
            PackageManager pm = context.getPackageManager();
            List<ResolveInfo> handlers = pm.queryIntentActivities(intent, PackageManager.GET_RESOLVED_FILTER);

            if (handlers == null || handlers.size() == 0) {
                return false;
            }

            for (ResolveInfo resolveInfo : handlers) {
                IntentFilter filter = resolveInfo.filter;
                if (filter == null) continue;
                if (filter.countDataAuthorities() == 0 || filter.countDataPaths() == 0) continue;
                if (resolveInfo.activityInfo == null) continue;
                return true;
            }
        } catch (RuntimeException e) {
            Log.e(TAG, "Runtime exception while getting specialized handlers");
        }
        return false;
    }

    /**
     * @return True if the current API is high enough, otherwise false.
     */
    private static boolean supportsCustomTabsApi() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1;
    }

    protected static boolean hasSupport(Activity activity) {
        return supportsCustomTabsApi() && CustomTabsHelper.getPackageNameToUse(activity) != null;
    }

    /**
     * Get an activity helper. When custom tabs are supported, it will use those. If custom tabs are not supported,
     * it will open urls in a new browser window.
     *
     * @param activity The activity that calls the custom tab.
     * @param callback The callback.
     *
     * @return The helper.
     */
    public static ActivityHelper initHelper(Activity activity, ActivityHelper.ConnectionCallback callback) {
        return initHelper(activity, true, callback);
    }

    /**
     * Get an activity helper. When custom tabs are supported, it will use those. If custom tabs are not supported,
     * it will open urls in a new browser window. When {@code nativeApp} is true, custom tabs will not be used when
     * an app is available that can handle a certain url. A normal intent will be launched then.
     *
     * @param activity  The activity that calls the custom tab.
     * @param nativeApp If the native app should be used if available.
     * @param callback  The callback.
     *
     * @return The helper.
     */
    public static ActivityHelper initHelper(Activity activity, boolean nativeApp, ActivityHelper.ConnectionCallback callback) {
        if(hasSupport(activity)) {
            return new HasTabActivityHelper(activity, nativeApp, callback);
        } else {
            return new NoTabActivityHelper(activity, callback);
        }
    }
}