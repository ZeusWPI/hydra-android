package be.ugent.zeus.hydra.common.ui.customtabs;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.List;

import be.ugent.zeus.hydra.common.utils.NetworkUtils;

/**
 * Custom tab implementation that directly launches the browser instead.
 *
 * @author Niko Strijbol
 */
class NoTabActivityHelper implements ActivityHelper {

    private final WeakReference<Activity> activity;
    private final ConnectionCallback connectionCallback;
    private int intentFlags;

    NoTabActivityHelper(Activity activity, @Nullable ConnectionCallback connectionCallback) {
        this.activity = new WeakReference<>(activity);
        this.connectionCallback = connectionCallback;
    }

    @Override
    public void setIntentFlags(int flags) {
        this.intentFlags = flags;
    }

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
    public void setShareMenu() {
    }

    @Override
    public boolean mayLaunchUrl(Uri uri, Bundle extras, List<Bundle> otherLikelyBundles) {
        return true;
    }

}
