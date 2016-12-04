package be.ugent.zeus.hydra.homefeed.content.schamper;

import be.ugent.zeus.hydra.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.models.schamper.Article;
import org.threeten.bp.Duration;
import org.threeten.bp.ZonedDateTime;

import static be.ugent.zeus.hydra.homefeed.content.HomeCard.CardType.SCHAMPER;

/**
 * Home card for {@link Article}.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
class SchamperCard extends HomeCard {

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
        return (int) Math.max(0, duration.toDays() * 8);
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