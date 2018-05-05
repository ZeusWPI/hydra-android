package be.ugent.zeus.hydra.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Niko Strijbol
 */
public class NetworkUtilsTest {

    @Test
    public void testIsConnected() {
        Context context = mock(Context.class);
        ConnectivityManager manager = mock(ConnectivityManager.class);
        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(manager);

        NetworkInfo networkInfo = mock(NetworkInfo.class);
        when(manager.getActiveNetworkInfo()).thenReturn(networkInfo);

        when(networkInfo.isConnected()).thenReturn(true);
        assertTrue(NetworkUtils.isConnected(context));

        when(networkInfo.isConnected()).thenReturn(false);
        assertFalse(NetworkUtils.isConnected(context));
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
