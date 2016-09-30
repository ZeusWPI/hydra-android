package be.ugent.zeus.hydra.models.cards;

import be.ugent.zeus.hydra.models.schamper.Article;
import org.threeten.bp.Duration;
import org.threeten.bp.ZonedDateTime;

import static be.ugent.zeus.hydra.models.cards.HomeCard.CardType.SCHAMPER;

/**
 * Created by feliciaan on 18/06/16.
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
        return (int) (1000 - (duration.toDays() * 100));
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
}