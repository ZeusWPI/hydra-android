package be.ugent.zeus.hydra.common.converter;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Niko Strijbol
 */
public class BooleanJsonAdapterTest {

    @Test
    public void write() {
        BooleanJsonAdapter adapter = new BooleanJsonAdapter();
        assertEquals(1, adapter.write(true));
        assertEquals(0, adapter.write(false));
    }

    @Test
    public void read() {
        BooleanJsonAdapter adapter = new BooleanJsonAdapter();
        assertTrue(adapter.read(1));
        assertFalse(adapter.read(0));
    }
}