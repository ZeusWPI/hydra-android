package be.ugent.zeus.hydra.domain.models.sko;

import be.ugent.zeus.hydra.domain.models.ModelTest;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

/**
 * @author Niko Strijbol
 */
public class ExhibitorTest extends ModelTest<Exhibitor> {

    public ExhibitorTest() {
        super(Exhibitor.class);
    }

    @Test
    public void equalsAndHash() {
        EqualsVerifier.forClass(Exhibitor.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .withOnlyTheseFields("name", "content")
                .verify();
    }
}