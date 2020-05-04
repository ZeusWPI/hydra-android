package be.ugent.zeus.hydra.common.utils;

import android.os.Build;
import androidx.annotation.RequiresApi;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Niko Strijbol
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class StringUtilsTest {

    @Test
    public void capitaliseFirst() {
        assertEquals("Hydra", StringUtils.capitaliseFirst("hydra"));
        assertEquals("HYDRA", StringUtils.capitaliseFirst("HYDRA"));
        assertEquals("HYDRA", StringUtils.capitaliseFirst("hYDRA"));
        assertEquals("65656565", StringUtils.capitaliseFirst("65656565"));
    }
}
