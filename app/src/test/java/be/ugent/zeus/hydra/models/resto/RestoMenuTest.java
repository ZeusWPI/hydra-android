package be.ugent.zeus.hydra.models.resto;

import be.ugent.zeus.hydra.testing.Utils;
import be.ugent.zeus.hydra.models.ModelTest;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

/**
 * @author Niko Strijbol
 */
public class RestoMenuTest extends ModelTest<RestoMenu> {

    public RestoMenuTest() {
        super(RestoMenu.class);
    }

    @Test
    public void equalsAndHash() {
        Utils.defaultVerifier(RestoMenu.class)
                .withOnlyTheseFields("date", "meals", "open")
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }
}