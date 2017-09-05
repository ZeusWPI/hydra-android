package be.ugent.zeus.hydra.data.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Test;
import org.threeten.bp.Instant;

import static org.junit.Assert.assertEquals;

/**
 * @author Niko Strijbol
 */
public class InstantThreeTenAdapterTest {

    private Gson gson;

    @Before
    public void setUp() {
        gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantThreeTenAdapter())
                .create();
    }

    @Test
    public void generalTest() throws Exception {
        Instant expected = Instant.now();
        String json = gson.toJson(expected);
        Instant actual = gson.fromJson(json, Instant.class);
        assertEquals(expected, actual);
    }

    @Test
    public void nullTest() throws Exception {
        String json = gson.toJson(null);
        Instant actual = gson.fromJson(json, Instant.class);
        assertEquals(null, actual);
    }

}