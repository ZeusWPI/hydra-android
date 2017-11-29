package be.ugent.zeus.hydra.domain.models.resto;

import be.ugent.zeus.hydra.domain.models.ModelTest;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

/**
 * @author Niko Strijbol
 */
public class ExtraFoodTest extends ModelTest<ExtraFood> {

    public ExtraFoodTest() {
        super(ExtraFood.class);
    }

    @Test
    public void equalsAndHash() {
        EqualsVerifier.forClass(ExtraFood.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();

    }
}