package be.ugent.zeus.hydra.data.models.minerva;

import be.ugent.zeus.hydra.testing.Utils;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

/**
 * @author Niko Strijbol
 */
public class WhatsNewTest {

    @Test
    public void equalsAndHash() {
        Utils.defaultVerifier(WhatsNew.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }
}