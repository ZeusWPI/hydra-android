package be.ugent.zeus.hydra.ui.main.homefeed.content.schamper;

import be.ugent.zeus.hydra.feed.Card;
import be.ugent.zeus.hydra.schamper.Article;
import be.ugent.zeus.hydra.ui.main.homefeed.content.FeedUtils;
import org.threeten.bp.Duration;
import org.threeten.bp.ZonedDateTime;

import static be.ugent.zeus.hydra.feed.Card.Type.SCHAMPER;

/**
 * Home card for {@link Article}.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
class SchamperCard extends Card {

    private final Article article;

    SchamperCard(Article article) {
        this.article = article;
    }

    Article getArticle() {
        return article;
    }

    @Override
    public int getPriority() {
        ZonedDateTime date = article.getPubDate();
        Duration duration = Duration.between(date, ZonedDateTime.now());
        // We only show the last month of schamper articles.
        return FeedUtils.lerp((int) duration.toDays(), 0, 30);
    }

    @Override
    public String getIdentifier() {
        return article.getIdentifier();
    }

    @Override
    public int getCardType() {
        return SCHAMPER;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchamperCard that = (SchamperCard) o;
        return java8.util.Objects.equals(article, that.article);
    }

    @Override
    public int hashCode() {
        return java8.util.Objects.hash(article);
    }
}