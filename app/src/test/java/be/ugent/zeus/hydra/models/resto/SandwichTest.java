package be.ugent.zeus.hydra.models.resto;

import be.ugent.zeus.hydra.models.ModelTest;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

/**
 * @author Niko Strijbol
 */
public class SandwichTest extends ModelTest<Sandwich> {

    public SandwichTest() {
        super(Sandwich.class);
    }

    @Test
    public void equalsAndHash() {
        EqualsVerifier.forClass(Sandwich.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }
}