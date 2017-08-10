package be.ugent.zeus.hydra.data.models.specialevent;

import be.ugent.zeus.hydra.testing.Utils;
import nl.jqno.equalsverifier.Warning;
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

    @Test
    public void equalsAndHash() {
        Utils.defaultVerifier(SpecialEventWrapper.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }
}