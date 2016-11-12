package be.ugent.zeus.hydra.models.cards;

import be.ugent.zeus.hydra.models.schamper.Article;
import be.ugent.zeus.hydra.utils.Objects;
import org.threeten.bp.Duration;
import org.threeten.bp.ZonedDateTime;

import static be.ugent.zeus.hydra.models.cards.HomeCard.CardType.SCHAMPER;

/**
 * Home card for {@link Article}.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class SchamperCard extends HomeCard {

    private Article article;

    public SchamperCard(Article article) {
        this.article = article;
    }

    @Override
    public int getPriority() {
        ZonedDateTime date = getArticle().getPubDate();
        Duration duration = Duration.between(date, ZonedDateTime.now());
        return (int) Math.max(0, duration.toDays() * 8);
    }

    @Override
    public int getCardType() {
        return SCHAMPER;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
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