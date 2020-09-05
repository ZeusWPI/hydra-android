package be.ugent.zeus.hydra.feed.cards.schamper;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Objects;

import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.PriorityUtils;
import be.ugent.zeus.hydra.schamper.Article;

import static be.ugent.zeus.hydra.feed.cards.Card.Type.SCHAMPER;

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
        OffsetDateTime date = article.getPubDate();
        Duration duration = Duration.between(date, OffsetDateTime.now());
        // We only show the last month of schamper articles.
        return PriorityUtils.lerp((int) duration.toDays(), 0, 30);
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
        return Objects.equals(article, that.article);
    }

    @Override
    public int hashCode() {
        return Objects.hash(article);
    }
}