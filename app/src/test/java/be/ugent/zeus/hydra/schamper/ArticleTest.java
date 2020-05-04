package be.ugent.zeus.hydra.schamper;

import be.ugent.zeus.hydra.common.ModelTest;
import be.ugent.zeus.hydra.testing.Utils;
import org.junit.Test;

/**
 * @author Niko Strijbol
 */
public class ArticleTest extends ModelTest<Article> {

    public ArticleTest() {
        super(Article.class);
    }

    @Test
    @Override
    public void equalsAndHash() {
        Utils.defaultVerifier(Article.class)
                .withOnlyTheseFields("link", "pubDate")
                .verify();
    }
}