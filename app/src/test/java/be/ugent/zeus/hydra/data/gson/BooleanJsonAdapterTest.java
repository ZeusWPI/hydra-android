package be.ugent.zeus.hydra.data.gson;

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
    public void generalTest() throws Exception {
        Boolean expected = true;
        String json = gson.toJson(expected);
        Boolean actual = gson.fromJson(json, Boolean.class);
        assertEquals(expected, actual);
    }

    @Test
    public void testWrite() throws Exception {
        String json = gson.toJson(true);
        assertEquals("1", json);
    }

    @Test
    public void testRead() throws Exception {
        Boolean value = gson.fromJson("1", Boolean.class);
        assertEquals(true, value);
    }

}