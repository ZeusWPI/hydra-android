package be.ugent.zeus.hydra.news;

import be.ugent.zeus.hydra.common.ModelTest;
import be.ugent.zeus.hydra.testing.Utils;
import org.junit.Test;

/**
 * @author Niko Strijbol
 */
public class NewsArticleTest extends ModelTest<NewsArticle> {
    public NewsArticleTest() {
        super(NewsArticle.class);
    }

    @Test
    public void equalsAndHash() {
        Utils.defaultVerifier(NewsArticle.class)
                .withNonnullFields("id")
                .withOnlyTheseFields("id")
                .verify();
    }
}