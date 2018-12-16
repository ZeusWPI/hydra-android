package be.ugent.zeus.hydra.feed.specialevent;

import be.ugent.zeus.hydra.specialevent.SpecialEventWrapper;
import be.ugent.zeus.hydra.testing.Utils;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

/**
 * @author Niko Strijbol
 */
public class SpecialEventWrapperTest {

    @Test
    public void equalsAndHash() {
        Utils.defaultVerifier(SpecialEventWrapper.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }
}