package be.ugent.zeus.hydra.data.models.library;

import org.junit.Test;

import static be.ugent.zeus.hydra.testing.Assert.assertSerialization;

/**
 * @author Niko Strijbol
 */
public class OpeningHoursTest {

    @Test
    public void serialize() {
        assertSerialization(OpeningHours.class);
    }

}