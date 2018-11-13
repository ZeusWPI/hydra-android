package be.ugent.zeus.hydra.association;

import be.ugent.zeus.hydra.association.news.UgentNewsArticle;
import be.ugent.zeus.hydra.common.ModelTest;
import be.ugent.zeus.hydra.testing.Utils;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

/**
 * @author Niko Strijbol
 */
public class UgentNewsArticleTest extends ModelTest<UgentNewsArticle> {

    public UgentNewsArticleTest() {
        super(UgentNewsArticle.class);
    }

    @Test
    public void equalsAndHash() {
        Utils.defaultVerifier(UgentNewsArticle.class)
                .withOnlyTheseFields("identifier")
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }
}