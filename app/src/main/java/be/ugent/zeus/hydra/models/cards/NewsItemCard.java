package be.ugent.zeus.hydra.models.cards;

import be.ugent.zeus.hydra.models.association.NewsItem;
import org.threeten.bp.Duration;
import org.threeten.bp.ZonedDateTime;

/**
 * Created by feliciaan on 18/06/16.
 */
public class NewsItemCard extends HomeCard {

    private NewsItem newsItem;

    public NewsItemCard(NewsItem newsItem) {
        this.newsItem = newsItem;
    }

    @Override
    public int getPriority() {
        int multiplier;
        if (getNewsItem().isHighlighted()) {
            multiplier = 45;
        } else {
            multiplier = 75;
        }
        ZonedDateTime date = getNewsItem().getDate();
        Duration duration = Duration.between(ZonedDateTime.now(), date);
        return (int) (1000 - (duration.toDays() * multiplier));
    }

    @Override
    public int getCardType() {
        return CardType.NEWS_ITEM;
    }

    public NewsItem getNewsItem() {
        return newsItem;
    }

    public void setNewsItem(NewsItem newsItem) {
        this.newsItem = newsItem;
    }
}
