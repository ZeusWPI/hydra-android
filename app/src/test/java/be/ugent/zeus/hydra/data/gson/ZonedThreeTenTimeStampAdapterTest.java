package be.ugent.zeus.hydra.data.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Test;
import org.threeten.bp.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Niko Strijbol
 */
public class ZonedThreeTenTimeStampAdapterTest {

    private Gson gson;

    @Before
    public void setUp() {
        gson = new GsonBuilder()
                .registerTypeAdapter(ZonedDateTime.class, new ZonedThreeTenTimeStampAdapter())
                .create();
    }

    @Test
    public void generalTest() {
        ZonedDateTime expected = ZonedDateTime.now();
        String json = gson.toJson(expected);
        ZonedDateTime actual = gson.fromJson(json, ZonedDateTime.class);
        assertTrue(expected.isEqual(actual));
    }

    @Test
    public void nullTest() {
        String json = gson.toJson(-1);
        ZonedDateTime actual = gson.fromJson(json, ZonedDateTime.class);
        assertEquals(null, actual);
    }

}