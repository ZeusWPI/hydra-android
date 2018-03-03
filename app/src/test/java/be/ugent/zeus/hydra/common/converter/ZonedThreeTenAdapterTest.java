package be.ugent.zeus.hydra.common.converter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Test;
import org.threeten.bp.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Niko Strijbol
 */
public class ZonedThreeTenAdapterTest {

    private Gson gson;

    @Before
    public void setUp() {
        gson = new GsonBuilder()
                .registerTypeAdapter(ZonedDateTime.class, new ZonedThreeTenAdapter())
                .create();
    }

    @Test
    public void generalTest() {
        ZonedDateTime expected = ZonedDateTime.now();
        String json = gson.toJson(expected);
        ZonedDateTime actual = gson.fromJson(json, ZonedDateTime.class);
        assertEquals(expected, actual);
    }

    @Test
    public void nullTest() {
        String json = gson.toJson(null);
        ZonedDateTime actual = gson.fromJson(json, ZonedDateTime.class);
        assertNull(actual);
    }

}