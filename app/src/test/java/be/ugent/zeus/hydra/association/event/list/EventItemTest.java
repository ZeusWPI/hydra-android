package be.ugent.zeus.hydra.association.event.list;

import be.ugent.zeus.hydra.testing.Utils;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

/**
 * @author Niko Strijbol
 */
public class EventItemTest {

    @Test
    public void equalsAndHash() {
        Utils.defaultVerifier(EventItem.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }

}
