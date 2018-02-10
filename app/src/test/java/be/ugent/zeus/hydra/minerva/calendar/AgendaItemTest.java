package be.ugent.zeus.hydra.minerva.calendar;

import be.ugent.zeus.hydra.common.ModelTest;
import be.ugent.zeus.hydra.testing.Utils;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

/**
 * @author Niko Strijbol
 */
public class AgendaItemTest extends ModelTest<AgendaItem> {

    public AgendaItemTest() {
        super(AgendaItem.class);
    }

    @Test
    public void equalsAndHash() {
        Utils.defaultVerifier(AgendaItem.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .withOnlyTheseFields("itemId")
                .verify();
    }
}