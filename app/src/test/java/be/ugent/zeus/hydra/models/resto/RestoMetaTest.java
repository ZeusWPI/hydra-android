package be.ugent.zeus.hydra.models.resto;

import be.ugent.zeus.hydra.models.ModelTest;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

/**
 * @author Niko Strijbol
 */
public class RestoMetaTest extends ModelTest<RestoMeta> {

    public RestoMetaTest() {
        super(RestoMeta.class);
    }

    @Test
    public void equalsAndHash() {
        EqualsVerifier.forClass(RestoMeta.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }
}