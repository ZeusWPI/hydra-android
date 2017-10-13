package be.ugent.zeus.hydra.data.models.association;

import be.ugent.zeus.hydra.data.models.ModelTest;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
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
    public void equalsAndHash() {
        EqualsVerifier.forClass(Association.class)
                .withOnlyTheseFields("internalName")
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }
}