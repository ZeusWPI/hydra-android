package be.ugent.zeus.hydra.minerva.announcement.sync;

import be.ugent.zeus.hydra.testing.Utils;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

/**
 * @author Niko Strijbol
 */
public class ApiWhatsNewTest {

    @Test
    public void equalsAndHash() {
        Utils.defaultVerifier(ApiWhatsNew.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }
}