package be.ugent.zeus.hydra.models.cards;

import be.ugent.zeus.hydra.models.association.NewsItem;
import be.ugent.zeus.hydra.utils.Objects;
import org.threeten.bp.Duration;
import org.threeten.bp.ZonedDateTime;

/**
 * Home card for {@link NewsItem}.
 *
 * @author Niko Strijbol
 * @author feliciaan
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsItemCard that = (NewsItemCard) o;
        return Objects.equals(newsItem, that.newsItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(newsItem);
    }
}