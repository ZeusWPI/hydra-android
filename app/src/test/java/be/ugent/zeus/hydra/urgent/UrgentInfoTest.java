package be.ugent.zeus.hydra.urgent;

import be.ugent.zeus.hydra.testing.Utils;
import org.junit.Test;

/**
 * @author Niko Strijbol
 */
public class UrgentInfoTest {

    @Test
    public void equalsAndHash() {
        Utils.defaultVerifier(UrgentInfo.class)
                .verify();
    }
}