package be.ugent.zeus.hydra.resto.extrafood;

import be.ugent.zeus.hydra.common.ModelTest;
import be.ugent.zeus.hydra.resto.extrafood.Food;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

/**
 * @author Niko Strijbol
 */
public class FoodTest extends ModelTest<Food> {

    public FoodTest() {
        super(Food.class);
    }

    @Test
    public void equalsAndHash() {
        EqualsVerifier.forClass(Food.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();

    }
}