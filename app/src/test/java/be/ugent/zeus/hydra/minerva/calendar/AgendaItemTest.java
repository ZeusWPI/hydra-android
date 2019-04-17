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
    @Override
    public void equalsAndHash() {
        Utils.defaultVerifier(AgendaItem.class)
                .withOnlyTheseFields("itemId")
                .verify();
    }
}