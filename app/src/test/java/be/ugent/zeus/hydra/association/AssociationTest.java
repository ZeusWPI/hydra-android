package be.ugent.zeus.hydra.association;

import be.ugent.zeus.hydra.common.ModelTest;
import be.ugent.zeus.hydra.testing.Utils;
import org.junit.Test;

/**
 * Unit tests for {@link Association}.
 *
 * @author Niko Strijbol
 */
public class AssociationTest extends ModelTest<Association> {

    public AssociationTest() {
        super(Association.class);
    }

    @Test
    @Override
    public void equalsAndHash() {
        Utils.defaultVerifier(Association.class)
                .withOnlyTheseFields("abbreviation")
                .verify();
    }
}