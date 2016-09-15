package be.ugent.zeus.hydra.utils.customtabs;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsCallback;

import java.util.List;

/**
 * Custom tab implementation that directly launches the browser instead.
 *
 * @author Niko Strijbol
 */
class NoTabActivityHelper implements ActivityHelper {

    private final Activity activity;
    private int intentFlags;

    /**
     * Package local constructor.
     */
    NoTabActivityHelper(Activity activity, ConnectionCallback connectionCallback) {
        this.activity = activity;
        connectionCallback.onCustomTabsConnected(this);
    }

    @Override
    public void setIntentFlags(int flags) {
        this.intentFlags = flags;
    }

    @Override
    public void setCallback(CustomTabsCallback callback) {}

    /**
     * Opens the URL in a new browser window.
     *
     * @param uri The uri to open.
     */
    @Override
    public void openCustomTab(Uri uri) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
        browserIntent.setFlags(this.intentFlags);
        activity.startActivity(browserIntent);
    }

    @Override
    public void unbindCustomTabsService(Activity activity) {}

    /**
     * Binds the activity to the Custom Tabs Service.
     *
     * @param activity The activity to be bound to the service.
     */
    @Override
    public void bindCustomTabsService(Activity activity) {}

    @Override
    public boolean mayLaunchUrl(Uri uri, Bundle extras, List<Bundle> otherLikelyBundles) {
        return true;
    }
}