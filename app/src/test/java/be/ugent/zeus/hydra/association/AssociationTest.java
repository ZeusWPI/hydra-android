package be.ugent.zeus.hydra.association;

import be.ugent.zeus.hydra.common.ModelTest;
import be.ugent.zeus.hydra.testing.Utils;
import org.junit.Test;

import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.assertEquals;

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
    public void getName() {
        Association full = generate(Association.class);
        assertEquals(full.getFullName(), full.getName());
        Association partial = generate(Association.class, "fullName");
        assertEquals(partial.getDisplayName(), partial.getName());
    }

    @Test
    @Override
    public void equalsAndHash() {
        Utils.defaultVerifier(Association.class)
                .withOnlyTheseFields("internalName")
                .verify();
    }
}