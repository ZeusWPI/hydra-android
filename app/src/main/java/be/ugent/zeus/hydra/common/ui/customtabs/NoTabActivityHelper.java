/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
