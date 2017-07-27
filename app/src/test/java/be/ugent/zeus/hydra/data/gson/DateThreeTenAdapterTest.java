package be.ugent.zeus.hydra.data.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Test;
import org.threeten.bp.LocalDate;

import static org.junit.Assert.assertEquals;

/**
 * @author Niko Strijbol
 */
public class DateThreeTenAdapterTest {

    private Gson gson;

    @Before
    public void setUp() {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new DateThreeTenAdapter())
                .create();
    }

    @Test
    public void generalTest() throws Exception {
        LocalDate expected = LocalDate.now();
        String json = gson.toJson(expected);
        LocalDate actual = gson.fromJson(json, LocalDate.class);
        assertEquals(expected, actual);
    }

    @Test
    public void nullTest() throws Exception {
        String json = gson.toJson(null);
        LocalDate actual = gson.fromJson(json, LocalDate.class);
        assertEquals(null, actual);
    }
}