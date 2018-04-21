package be.ugent.zeus.hydra.resto.extrafood;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

/**
 * @author Niko Strijbol
 */
public class ExtraFoodTest {

    @Test
    public void equalsAndHash() {
        EqualsVerifier.forClass(ExtraFood.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();

    }
}