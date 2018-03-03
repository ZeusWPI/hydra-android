package be.ugent.zeus.hydra.common.converter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Test;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;

import static org.junit.Assert.*;

/**
 * @author Niko Strijbol
 */
public class ZonedThreeTenUTCAdapterTest {

    private Gson gson;

    @Before
    public void setUp() {
        gson = new GsonBuilder()
                .registerTypeAdapter(ZonedDateTime.class, new ZonedThreeTenUTCAdapter())
                .create();
    }

    @Test
    public void generalTest() {
        ZonedDateTime expected = LocalDateTime.now().atZone(ZoneOffset.UTC);
        String json = gson.toJson(expected);
        ZonedDateTime actual = gson.fromJson(json, ZonedDateTime.class);
        assertTrue(expected.isEqual(actual));
    }

    @Test
    public void nullTest() {
        String json = gson.toJson(null);
        ZonedDateTime actual = gson.fromJson(json, ZonedDateTime.class);
        assertNull(actual);
    }

}