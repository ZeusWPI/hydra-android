package be.ugent.zeus.hydra.models.resto;

import be.ugent.zeus.hydra.models.ModelTest;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

/**
 * @author Niko Strijbol
 */
public class RestoMealTest extends ModelTest<RestoMeal> {

    public RestoMealTest() {
        super(RestoMeal.class);
    }

    @Test
    public void equalsAndHash() {
        EqualsVerifier.forClass(RestoMeal.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();

    }
}