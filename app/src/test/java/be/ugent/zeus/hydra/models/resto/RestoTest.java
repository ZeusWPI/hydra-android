package be.ugent.zeus.hydra.models.resto;

import be.ugent.zeus.hydra.models.ModelTest;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

/**
 * @author Niko Strijbol
 */
public class RestoTest extends ModelTest<Resto> {

    public RestoTest() {
        super(Resto.class);
    }

    @Test
    public void equalsAndHash() {
        EqualsVerifier.forClass(Resto.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }
}