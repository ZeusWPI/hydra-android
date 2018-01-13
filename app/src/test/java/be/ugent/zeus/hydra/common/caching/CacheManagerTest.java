package be.ugent.zeus.hydra.common.caching;

import android.content.Context;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Niko Strijbol
 */
public class CacheManagerTest {

    @Test
    public void testSingleton() {
        Context context = mock(Context.class);
        when(context.getCacheDir()).thenReturn(mock(File.class));

        Cache instanceOne = CacheManager.defaultCache(context);
        Cache instanceTwo = CacheManager.defaultCache(context);

        assertSame(instanceOne, instanceTwo);
    }

}