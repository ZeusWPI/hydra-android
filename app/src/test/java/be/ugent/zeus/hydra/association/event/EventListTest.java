package be.ugent.zeus.hydra.association.event;

import be.ugent.zeus.hydra.testing.Utils;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

/**
 * @author Niko Strijbol
 */
public class EventListTest {

    @Test
    public void equalsAndHash() {
        Utils.defaultVerifier(EventList.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }

}