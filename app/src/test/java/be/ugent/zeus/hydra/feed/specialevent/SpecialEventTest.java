package be.ugent.zeus.hydra.feed.specialevent;

import be.ugent.zeus.hydra.specialevent.SpecialEvent;
import be.ugent.zeus.hydra.testing.Utils;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

/**
 * @author Niko Strijbol
 */
public class SpecialEventTest {

    @Test
    public void equalsAndHash() {
        Utils.defaultVerifier(SpecialEvent.class)
                .withIgnoredFields("development")
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }

}