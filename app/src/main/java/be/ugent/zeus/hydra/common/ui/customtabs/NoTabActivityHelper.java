package be.ugent.zeus.hydra.common.ui.customtabs;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsCallback;
import be.ugent.zeus.hydra.utils.NetworkUtils;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Custom tab implementation that directly launches the browser instead.
 *
 * @author Niko Strijbol
 */
class NoTabActivityHelper implements ActivityHelper {

    private final WeakReference<Activity> activity;
    private int intentFlags;
    private final ConnectionCallback connectionCallback;

    /**
     * Package local constructor.
     */
    NoTabActivityHelper(Activity activity, @Nullable ConnectionCallback connectionCallback) {
        this.activity = new WeakReference<>(activity);
        this.connectionCallback = connectionCallback;
    }

    @Override
    public void setIntentFlags(int flags) {
        this.intentFlags = flags;
    }

    @Override
    public void setCallback(@Nullable CustomTabsCallback callback) {}

    /**
     * Opens the URL in a new browser window.
     *
     * @param uri The uri to open.
     */
    @Override
    public void openCustomTab(Uri uri) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
        browserIntent.setFlags(this.intentFlags);
        NetworkUtils.maybeLaunchIntent(activity.get(), browserIntent);
    }

    @Override
    public void unbindCustomTabsService(Activity activity) {
        if (connectionCallback != null) {
            connectionCallback.onCustomTabsDisconnected(this);
        }
    }

    @Override
    public void bindCustomTabsService(Activity activity) {
        if (connectionCallback != null) {
            connectionCallback.onCustomTabsConnected(this);
        }
    }

    @Override
    public void setShareMenu() {}

    @Override
    public boolean mayLaunchUrl(Uri uri, Bundle extras, List<Bundle> otherLikelyBundles) {
        return true;
    }

}