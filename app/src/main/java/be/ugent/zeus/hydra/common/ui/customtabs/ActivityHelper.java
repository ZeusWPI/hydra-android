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
import android.net.Uri;

/**
 * Interface for the custom tab helper.
 *
 * @author Niko Strijbol
 */
public interface ActivityHelper {

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
     */
    void setShareMenu();

    /**
     * Callback for the connection. It is not guaranteed when this is called by the ActivityHelper, so you cannot rely
     * on methods having returned yet.
     */
    interface ConnectionCallback {
        void onCustomTabsConnected(ActivityHelper helper);

        void onCustomTabsDisconnected(ActivityHelper helper);
    }
}
