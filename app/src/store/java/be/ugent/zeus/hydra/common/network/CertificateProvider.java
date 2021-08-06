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
package be.ugent.zeus.hydra.common.network;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

/**
 * @author Niko Strijbol
 */
public class CertificateProvider {

    private static final String TAG = "CertificateProvider";

    public static void installProvider(Context context) {
        Log.i(TAG, "Installing Play Services to enable TLSv1.2");
        try {
            ProviderInstaller.installIfNeeded(context);
        } catch (GooglePlayServicesRepairableException e) {
            Log.w(TAG, "Play Services are outdated", e);
            // Prompt the user to install/update/enable Google Play services.
            GoogleApiAvailability.getInstance().showErrorNotification(context, e.getConnectionStatusCode());
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, "Unable to install provider, SSL will not work", e);
        }
    }
}
