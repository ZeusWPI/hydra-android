package be.ugent.zeus.hydra.common.converter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Niko Strijbol
 */
public class BooleanJsonAdapterTest {

    private Gson gson;

    @Before
    public void setUp() {
        gson = new GsonBuilder()
                .registerTypeAdapter(Boolean.class, new BooleanJsonAdapter())
                .create();
    }

    @Test
    public void generalTest() {
        String json = gson.toJson(true);
        Boolean actual = gson.fromJson(json, Boolean.class);
        assertEquals(true, actual);
    }

    @Test
    public void testWrite() {
        String json = gson.toJson(true);
        assertEquals("1", json);
    }

    @Test
    public void testRead() {
        Boolean value = gson.fromJson("1", Boolean.class);
        assertEquals(true, value);
    }

}