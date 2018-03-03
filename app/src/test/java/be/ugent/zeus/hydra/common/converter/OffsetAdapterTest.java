package be.ugent.zeus.hydra.common.converter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Test;
import org.threeten.bp.OffsetDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Niko Strijbol
 */
public class OffsetAdapterTest {

    private Gson gson;

    @Before
    public void setUp() {
        gson = new GsonBuilder()
                .registerTypeAdapter(OffsetDateTime.class, new DateTypeConverters.GsonOffset())
                .create();
    }

    @Test
    public void generalTest() {
        OffsetDateTime expected = OffsetDateTime.now();
        String json = gson.toJson(expected);
        OffsetDateTime actual = gson.fromJson(json, OffsetDateTime.class);
        assertEquals(expected, actual);
    }

    @Test
    public void nullTest() {
        String json = gson.toJson(null);
        OffsetDateTime actual = gson.fromJson(json, OffsetDateTime.class);
        assertNull(actual);
    }
}