package be.ugent.zeus.hydra.common.ui.customtabs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.browser.customtabs.CustomTabsService;

import java.util.ArrayList;
import java.util.List;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.utils.NetworkUtils;

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
    private static final String PACKAGE_STABLE = "com.android.chrome";
    private static final String PACKAGE_BETA = "com.chrome.beta";
    private static final String PACKAGE_DEV = "com.chrome.dev";
    private static final String PACKAGE_LOCAL = "com.google.android.apps.chrome";

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
    static String getPackageNameToUse(Context context) {

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

            if (handlers.isEmpty()) {
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

    private static boolean hasSupport(Context context) {
        return CustomTabsHelper.getPackageNameToUse(context) != null;
    }

    /**
     * Get an activity helper. When custom tabs are supported, it will use those. If custom tabs are not supported,
     * it will open urls in a new browser window. When {@code nativeApp} is true, custom tabs will not be used when
     * an app is available that can handle a certain url. A normal intent will be launched then.
     *
     * @param activity  The activity that calls the custom tab.
     * @param callback  The callback.
     *
     * @return The helper.
     */
    public static ActivityHelper initHelper(Activity activity, @Nullable ActivityHelper.ConnectionCallback callback) {
        if(hasSupport(activity)) {
            return new HasTabActivityHelper(activity, callback);
        } else {
            return new NoTabActivityHelper(activity, callback);
        }
    }

    /**
     * Open an URI in a Custom Tab if supported. Otherwise, an attempt is made to open a browser.
     *
     * @param context Context to use.
     * @param uri The URI to open.
     */
    public static void openUri(Context context, Uri uri) {
        if (hasSupport(context)) {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            //noinspection deprecation
            builder.setToolbarColor(context.getResources().getColor(R.color.hydra_primary_color));
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(context, uri);
        } else {
            NetworkUtils.maybeLaunchBrowser(context, uri.toString());
        }
    }
}
