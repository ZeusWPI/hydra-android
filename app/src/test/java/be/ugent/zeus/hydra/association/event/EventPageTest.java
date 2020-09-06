package be.ugent.zeus.hydra.association.event;

import be.ugent.zeus.hydra.testing.Utils;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

/**
 * @author Niko Strijbol
 */
public class EventPageTest {

    @Test
    public void equalsAndHash() {
        Utils.defaultVerifier(EventPage.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }

}