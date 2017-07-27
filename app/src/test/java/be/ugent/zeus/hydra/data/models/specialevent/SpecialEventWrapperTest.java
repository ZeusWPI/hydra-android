package be.ugent.zeus.hydra.data.models.specialevent;

import org.junit.Test;

import static be.ugent.zeus.hydra.testing.Assert.assertSerialization;

/**
 * @author Niko Strijbol
 */
public class SpecialEventWrapperTest {

    @Test
    public void serialize() {
        assertSerialization(SpecialEventWrapper.class);
    }

}