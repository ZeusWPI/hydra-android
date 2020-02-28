package be.ugent.zeus.hydra.common.ui.customtabs;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.*;
import android.util.Log;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.utils.ColourUtils;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Helper for activities that use custom tabs.
 *
 * Taken from https://github.com/hitherejoe/Tabby
 *
 * @author Niko Strijbol
 */
class HasTabActivityHelper implements ActivityHelper {

    private static final String TAG = "HasTabActivityHelper";

    private final ConnectionCallback connectionCallback;
    private final WeakReference<Activity> activity;

    private boolean showShareMenu;
    private int intentFlags;

    private CustomTabsSession customTabsSession;
    private WeakReference<CustomTabsClient> client;
    private CustomTabsServiceConnection connection;

    /**
     * Package local constructor.
     */
    HasTabActivityHelper(Activity activity, @Nullable ConnectionCallback connectionCallback) {
        this.activity = new WeakReference<>(activity);
        this.connectionCallback = connectionCallback;
    }

    @Override
    public void setIntentFlags(int flags) {
        this.intentFlags = flags;
    }

    /**
     * Opens the URL on a Custom Tab if possible. Otherwise, fall back to opening it on a WebView
     *
     * @param uri the Uri to be opened
     */
    @Override
    public void openCustomTab(Uri uri) {

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder(customTabsSession);
        //Set the theme color
        builder.setToolbarColor(ColourUtils.resolveColour(activity.get(), R.attr.colorPrimarySurface));

        Set<String> nat = getNativeAppPackage(activity.get(), uri);
        if (!nat.isEmpty()) {
            Log.d(TAG, "Using normal intent because of native app, i.e. " + nat.iterator().next());
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
            browserIntent.setFlags(this.intentFlags);
            activity.get().startActivity(browserIntent);
        } else {
            String packageName = CustomTabsHelper.getPackageNameToUse(activity.get());
            Log.d(TAG, "No native app or native apps disabled, launching custom tab using " + packageName);
            //Add sharing if needed
            if (showShareMenu) {
                builder.addDefaultShareMenuItem();
            }
            //Get the intent
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.intent.setFlags(this.intentFlags);
            customTabsIntent.intent.setPackage(packageName);
            customTabsIntent.launchUrl(activity.get(), uri);
       }
    }

    /**
     * Unbinds the Activity from the Custom Tabs Service
     *
     * @param activity the activity that is connected to the service
     */
    @Override
    public void unbindCustomTabsService(Activity activity) {
        if (connection == null) {
            return;
        }
        activity.unbindService(connection);
        client = null;
        customTabsSession = null;
    }

    /**
     * Creates or retrieves an exiting session.
     *
     * @return The session.
     */
    private CustomTabsSession getSession() {

        if (client == null) {
            customTabsSession = null;
        } else if (customTabsSession == null) {
            customTabsSession = client.get().newSession(null);
        }

        return customTabsSession;
    }

    /**
     * Binds the activity to the Custom Tabs Service.
     *
     * @param activity The activity to be bound to the service.
     */
    @Override
    public void bindCustomTabsService(Activity activity) {

        //If it is already bound.
        if (client != null) {
            return;
        }

        String packageName = CustomTabsHelper.getPackageNameToUse(activity);

        connection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(@NonNull ComponentName name, @NonNull CustomTabsClient client) {
                HasTabActivityHelper.this.client = new WeakReference<>(client);
                try {
                    HasTabActivityHelper.this.client.get().warmup(0L);
                } catch (IllegalStateException e) {
                    // Ignore error when starting the warm up service.
                    // The reason why this exception occurs is probably as follows. On Android 8 and above, the app
                    // can no longer start services when it is in the background. However, to warm the Custom Tabs, a
                    // service has to be started. There is a very small but real possibility that the user opens an
                    // activity with custom tabs and navigates away from the app after the connection has been
                    // initialized, but before the actual warm-up service has started.
                    // See also e.g. https://github.com/openid/AppAuth-Android/issues/425
                    Log.e(TAG, "onCustomTabsServiceConnected: warm-up service startup race condition.", e);
                }
                if (connectionCallback != null) {
                    connectionCallback.onCustomTabsConnected(HasTabActivityHelper.this);
                }
                //Initialize a session as soon as possible.
                getSession();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                client = null;
                if (connectionCallback != null) {
                    connectionCallback.onCustomTabsDisconnected(HasTabActivityHelper.this);
                }
            }
        };

        CustomTabsClient.bindCustomTabsService(activity, packageName, connection);
    }

    @Override
    public void setShareMenu() {
        this.showShareMenu = true;
    }

    @Override
    public boolean mayLaunchUrl(Uri uri, Bundle extras, List<Bundle> otherLikelyBundles) {
        if (client == null) {
            return false;
        }

        CustomTabsSession session = getSession();
        return session != null && session.mayLaunchUrl(uri, extras, otherLikelyBundles);
    }

    private static Set<String> getNativeAppPackage(Context context, Uri uri) {
        PackageManager pm = context.getPackageManager();

        //Get all Apps that resolve a generic url
        Intent browserActivityIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com"));
        Set<String> genericResolvedList = extractPackageNames(pm.queryIntentActivities(browserActivityIntent, 0));

        //Get all apps that resolve the specific Url
        Intent specializedActivityIntent = new Intent(Intent.ACTION_VIEW, uri);
        Set<String> resolvedSpecializedList = extractPackageNames(pm.queryIntentActivities(specializedActivityIntent, 0));

        //Keep only the Urls that resolve the specific, but not the generic urls
        resolvedSpecializedList.removeAll(genericResolvedList);

        return resolvedSpecializedList;
    }

    private static Set<String> extractPackageNames(List<ResolveInfo> resolveInfos) {
        Set<String> packageNameSet = new HashSet<>();
        for (ResolveInfo ri : resolveInfos) {
            packageNameSet.add(ri.activityInfo.packageName);
        }
        return packageNameSet;
    }
}
