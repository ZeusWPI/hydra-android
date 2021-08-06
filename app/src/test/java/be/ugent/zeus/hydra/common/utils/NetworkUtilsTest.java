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

package be.ugent.zeus.hydra.common.utils;

import android.content.Context;
import android.net.*;
import android.os.Build;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Niko Strijbol
 */
public class NetworkUtilsTest {

    @Test
    @SuppressWarnings("deprecation")
    public void testIsConnected() {
        Context context = mock(Context.class);
        ConnectivityManager manager = mock(ConnectivityManager.class);
        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(manager);

        if (Build.VERSION.SDK_INT < 29) {
            NetworkInfo networkInfo = mock(NetworkInfo.class);
            when(manager.getActiveNetworkInfo()).thenReturn(networkInfo);

            when(networkInfo.isConnected()).thenReturn(true);
            assertTrue(NetworkUtils.isConnected(context));

            when(networkInfo.isConnected()).thenReturn(false);
            assertFalse(NetworkUtils.isConnected(context));
        } else {
            when(manager.getActiveNetwork()).thenReturn(null);
            assertFalse(NetworkUtils.isConnected(context));

            Network network = mock(Network.class);
            NetworkCapabilities capabilities = mock(NetworkCapabilities.class);
            when(manager.getActiveNetwork()).thenReturn(network);
            when(manager.getNetworkCapabilities(network)).thenReturn(capabilities);
            assertFalse(NetworkUtils.isConnected(context));

            when(capabilities.hasCapability(any())).thenReturn(true);
            assertTrue(NetworkUtils.isConnected(context));
        }
    }

    @Test
    public void testMeteredConnection() {
        Context context = mock(Context.class);
        ConnectivityManager manager = mock(ConnectivityManager.class);
        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(manager);

        when(manager.isActiveNetworkMetered()).thenReturn(true);
        assertTrue(NetworkUtils.isMeteredConnection(context));
        when(manager.isActiveNetworkMetered()).thenReturn(false);
        assertFalse(NetworkUtils.isMeteredConnection(context));
    }
}
