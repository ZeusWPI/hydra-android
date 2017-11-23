package be.ugent.zeus.hydra.utils;

import android.os.Build;
import android.support.annotation.RequiresApi;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

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

    @Test
    public void inputStreamTest() {
        String testString = "test\nstring";
        InputStream stream = new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8));
        String result = StringUtils.convertStreamToString(stream);
        assertEquals(testString, result);
    }

    @Test
    public void inputStreamTestUtf8() {
        String testString = "test\nstring¨$^$ù$^$^$^&$^é$&é";
        InputStream stream = new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8));
        String result = StringUtils.convertStreamToString(stream);
        assertEquals(testString, result);
    }

    @Test
    public void inputStreamTestEmpty() {
        String testString = "";
        InputStream stream = new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8));
        String result = StringUtils.convertStreamToString(stream);
        assertEquals(testString, result);
    }

    @Test
    public void generateAcronymFor() {
        String[] values = new String[] {
                "Algoritmen en datastructuren III",
                "Parallelle en gedistribueerde computersystemen",
                "Recht van de intellectuele eigendom",
                "Betonkunde 1"
        };
        // Note: these are NOT good abbreviations, just what the algorithm currently does
        String[] expectedAcronyms = new String[] {
                "AEDI",
                "PEGC",
                "RVDIE",
                "B1"
        };

        for (int i = 0; i < values.length; i++) {
            assertEquals(expectedAcronyms[i], StringUtils.generateAcronymFor(values[i]));
        }
    }
}