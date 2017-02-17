package be.ugent.zeus.hydra.models.schamper;

import be.ugent.zeus.hydra.models.ModelTest;
import be.ugent.zeus.hydra.testing.Utils;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

/**
 * @author Niko Strijbol
 */
public class ArticleTest extends ModelTest<Article> {

    public ArticleTest() {
        super(Article.class);
    }

    @Test
    public void equalsAndHash() {
        Utils.defaultVerifier(Article.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .withOnlyTheseFields("link", "pubDate")
                .verify();
    }
}