package be.ugent.zeus.hydra.models.cards;

import be.ugent.zeus.hydra.models.schamper.Article;
import org.joda.time.DateTime;
import org.joda.time.Duration;

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
        DateTime jodadate = new DateTime(this.getArticle().getPubDate());
        Duration duration = new Duration(jodadate, new DateTime());
        return (int) (1000 - (duration.getStandardDays()*100));
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