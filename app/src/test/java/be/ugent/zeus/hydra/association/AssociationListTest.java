package be.ugent.zeus.hydra.association;

import be.ugent.zeus.hydra.testing.Utils;
import org.junit.Test;

/**
 * Unit tests for {@link Association}.
 *
 * @author Niko Strijbol
 */
public class AssociationListTest {

    @Test
    public void equalsAndHash() {
        Utils.defaultVerifier(AssociationList.class).verify();
    }
}