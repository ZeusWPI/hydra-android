package be.ugent.zeus.hydra.models.association;

import be.ugent.zeus.hydra.testing.Utils;
import be.ugent.zeus.hydra.models.ModelTest;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

/**
 * @author Niko Strijbol
 */
public class EventTest extends ModelTest<Event> {

    public EventTest() {
        super(Event.class);
    }

    @Test
    public void equalsAndHash() {
        Utils.defaultVerifier(Event.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }
}