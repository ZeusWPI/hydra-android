package be.ugent.zeus.hydra.common.converter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Test;
import org.threeten.bp.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
    public void generalTest() {
        LocalDate expected = LocalDate.now();
        String json = gson.toJson(expected);
        LocalDate actual = gson.fromJson(json, LocalDate.class);
        assertEquals(expected, actual);
    }

    @Test
    public void nullTestRead() {
        String json = gson.toJson(null);
        LocalDate actual = gson.fromJson(json, LocalDate.class);
        assertNull(actual);
    }
}