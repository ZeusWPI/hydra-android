package be.ugent.zeus.hydra.utils.customtabs;

import android.app.Activity;
import android.content.ComponentName;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.*;

import be.ugent.zeus.hydra.utils.ViewUtils;

import java.util.List;

/**
 * Helper for activities that use custom tabs.
 *
 * Taken from https://github.com/hitherejoe/Tabby
 *
 * @author Niko Strijbol
 */
class HasTabActivityHelper implements ActivityHelper {

    private CustomTabsSession customTabsSession;
    private CustomTabsClient client;
    private CustomTabsServiceConnection connection;
    private CustomTabsCallback callback;

    private ConnectionCallback connectionCallback;

    private Activity activity;

    private int intentFlags;

    HasTabActivityHelper(Activity activity, ConnectionCallback connectionCallback) {
        this.activity = activity;
        this.connectionCallback = connectionCallback;
    }

    @Override
    public void setIntentFlags(int flags) {
        this.intentFlags = flags;
    }

    @Override
    public void setCallback(CustomTabsCallback callback) {
        this.callback = callback;
    }

    /**
     * Opens the URL on a Custom Tab if possible. Otherwise fall back to opening it on a WebView
     *
     * @param uri the Uri to be opened
     */
    @Override
    public void openCustomTab(Uri uri) {

        //Else we do the custom tabs.
        String packageName = CustomTabsHelper.getPackageNameToUse(activity);

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder(customTabsSession);
        //Set the theme color
        builder.setToolbarColor(ViewUtils.getPrimaryColor(activity));

        //Get the intent
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.intent.setFlags(this.intentFlags);
        customTabsIntent.launchUrl(activity, uri);
    }

    /**
     * Unbinds the Activity from the Custom Tabs Service
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
            customTabsSession = client.newSession(this.callback);
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
            public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
                HasTabActivityHelper.this.client = client;
                HasTabActivityHelper.this.client.warmup(0L);
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
    public boolean mayLaunchUrl(Uri uri, Bundle extras, List<Bundle> otherLikelyBundles) {
        if (client == null) {
            return false;
        }

        CustomTabsSession session = getSession();
        return session != null && session.mayLaunchUrl(uri, extras, otherLikelyBundles);

    }
}