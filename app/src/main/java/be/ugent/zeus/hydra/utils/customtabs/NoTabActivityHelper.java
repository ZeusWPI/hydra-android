package be.ugent.zeus.hydra.utils.customtabs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsCallback;
import be.ugent.zeus.hydra.utils.NetworkUtils;

import java.util.List;

/**
 * Custom tab implementation that directly launches the browser instead.
 *
 * @author Niko Strijbol
 */
class NoTabActivityHelper implements ActivityHelper {

    private final Context context;
    private int intentFlags;
    private final ConnectionCallback connectionCallback;

    /**
     * Package local constructor.
     */
    NoTabActivityHelper(Context context, @Nullable ConnectionCallback connectionCallback) {
        this.context = context.getApplicationContext();
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
        NetworkUtils.maybeLaunchIntent(context, browserIntent);
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